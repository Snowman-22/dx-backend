package com.snowman.team2.domain.reco.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snowman.team2.domain.auth.entity.UserEntity;
import com.snowman.team2.domain.auth.repository.UserRepository;
import com.snowman.team2.domain.cart.entity.CartEntity;
import com.snowman.team2.domain.cart.repository.CartRepository;
import com.snowman.team2.domain.chat.entity.ChatEntity;
import com.snowman.team2.domain.chat.repository.ChatRepository;
import com.snowman.team2.domain.product.entity.ProductEntity;
import com.snowman.team2.domain.product.repository.ProductRepository;
import com.snowman.team2.domain.reco.dto.request.SelectRecommendationRequestDTO;
import com.snowman.team2.domain.reco.dto.response.RecommendationDTO;
import com.snowman.team2.domain.reco.entity.RecommendationEntity;
import com.snowman.team2.domain.reco.repository.RecommendationRepository;
import com.snowman.team2.global.exception.ErrorCode;
import com.snowman.team2.global.exception.exceptionType.BadRequestException;
import com.snowman.team2.global.exception.exceptionType.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final ObjectMapper objectMapper;

    private void checkChatOwner(ChatEntity chat, Long userId) {
        // chat prestart 단계에서는 user가 null일 수 있음
        if (chat.getUser() == null || chat.getUser().getUserId() == null || !chat.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedException(ErrorCode.ACCESS_DENIED, "접근 권한이 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<RecommendationDTO> getRecommendations(String chatId, Long userId) {
        ChatEntity chat = chatRepository.findByChatConvId(chatId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "채팅을 찾을 수 없습니다."));

        checkChatOwner(chat, userId);

        List<RecommendationEntity> recs = recommendationRepository.findAllByChat_ChatId(chat.getChatId());
        return recs.stream()
                .map(r -> new RecommendationDTO(
                        r.getRecommendationId(),
                        chat.getChatConvId(),
                        r.getReason(),
                        r.getProducts(),
                        r.getIsSelected()
                ))
                .toList();
    }

    /**
     * 선택된 recommendation의 제품 목록을 사용자 cart에 담는다.
     */
    @Transactional
    public void selectRecommendation(String chatId, SelectRecommendationRequestDTO request, Long userId) {
        ChatEntity chat = chatRepository.findByChatConvId(chatId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "채팅을 찾을 수 없습니다."));

        checkChatOwner(chat, userId);

        RecommendationEntity recommendation = recommendationRepository
                .findByChat_ChatIdAndRecommendationId(chat.getChatId(), request.recommendationId())
                .orElseThrow(() -> new BadRequestException(
                        ErrorCode.DATA_NOT_EXIST,
                        "지정한 recommendation이 해당 chat에 존재하지 않습니다."
                ));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "사용자를 찾을 수 없습니다."));

        List<Long> productIds = extractProductIds(recommendation.getProducts());
        if (productIds.isEmpty()) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "선택한 recommendation에 담긴 제품이 없습니다.");
        }

        int addedCount = 0;
        for (Long productId : productIds) {
            if (productId == null) {
                continue;
            }
            if (cartRepository.existsByUser_UserIdAndProduct_ProductIdAndIsDeleteFalse(userId, productId)) {
                continue;
            }
            ProductEntity product = productRepository.findById(productId)
                    .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "상품을 찾을 수 없습니다. productId=" + productId));

            cartRepository.save(CartEntity.builder()
                    .user(user)
                    .product(product)
                    .quantity(1)
                    .isDelete(false)
                    .createDate(LocalDateTime.now())
                    .build());
            addedCount++;
        }

        if (addedCount == 0) {
            throw new BadRequestException(ErrorCode.DATA_ALREADY_EXIST, "이미 선택하여 카트에 담긴 추천입니다.");
        }
    }

    @Transactional
    public Map<String, Object> saveAndAttachRecommendationIds(Long chatId, String chatConvId, Map<String, Object> fastApiData) {
        if (fastApiData == null) {
            return Map.of();
        }

        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "채팅을 찾을 수 없습니다."));

        Object recommendationsRaw = fastApiData.get("recommendations");
        if (!(recommendationsRaw instanceof List<?> recommendations)) {
            // 추천 리스트가 없는 응답은 그대로 전달
            return fastApiData;
        }

        // 최신 추천 결과로 교체한다.
        recommendationRepository.deleteAllByChat_ChatId(chatId);

        List<RecommendationEntity> entitiesToSave = new ArrayList<>();
        List<Map<String, Object>> normalizedRecommendations = new ArrayList<>();
        for (Object item : recommendations) {
            if (!(item instanceof Map<?, ?> mapItem)) {
                continue;
            }

            String reason = toNullableString(mapItem.get("reason"));
            Object productsObject = mapItem.get("products");
            String products = toJsonString(productsObject);

            entitiesToSave.add(RecommendationEntity.builder()
                    .chat(chat)
                    .isSelected(false)
                    .reason(reason)
                    .products(products)
                    .build());

            Map<String, Object> normalized = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : mapItem.entrySet()) {
                if (entry.getKey() != null) {
                    normalized.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }
            normalizedRecommendations.add(normalized);
        }

        List<RecommendationEntity> saved = recommendationRepository.saveAll(entitiesToSave);

        List<Map<String, Object>> withIds = new ArrayList<>();
        for (int i = 0; i < saved.size(); i++) {
            RecommendationEntity savedEntity = saved.get(i);
            Map<String, Object> row = i < normalizedRecommendations.size()
                    ? new LinkedHashMap<>(normalizedRecommendations.get(i))
                    : new LinkedHashMap<>();
            row.put("recommendation_id", savedEntity.getRecommendationId());
            row.put("chat_uuid", chatConvId);
            row.put("is_selected", savedEntity.getIsSelected());
            withIds.add(row);
        }

        Map<String, Object> responseData = new LinkedHashMap<>(fastApiData);
        responseData.put("recommendations", withIds);
        return responseData;
    }

    private String toNullableString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String toJsonString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String str) {
            return str;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("products 직렬화에 실패했습니다.", e);
        }
    }

    private List<Long> extractProductIds(String productsRaw) {
        if (productsRaw == null || productsRaw.isBlank()) {
            return List.of();
        }

        try {
            JsonNode root = objectMapper.readTree(productsRaw);
            Set<Long> productIds = new HashSet<>();
            collectProductIds(root, productIds);
            return new ArrayList<>(productIds);
        } catch (JsonProcessingException ignored) {
            try {
                List<Long> directList = objectMapper.readValue(productsRaw, new TypeReference<List<Long>>() {
                });
                return directList == null ? List.of() : directList.stream().filter(v -> v != null).toList();
            } catch (JsonProcessingException e) {
                throw new BadRequestException(ErrorCode.INVALID_FORMAT, "products 포맷을 해석할 수 없습니다.");
            }
        }
    }

    private void collectProductIds(JsonNode node, Set<Long> productIds) {
        if (node == null || node.isNull()) {
            return;
        }

        if (node.isIntegralNumber()) {
            productIds.add(node.asLong());
            return;
        }

        if (node.isTextual()) {
            try {
                productIds.add(Long.parseLong(node.asText()));
            } catch (NumberFormatException ignored) {
            }
            return;
        }

        if (node.isObject()) {
            JsonNode productIdNode = node.get("product_id");
            if (productIdNode != null && productIdNode.isIntegralNumber()) {
                productIds.add(productIdNode.asLong());
            }
            JsonNode productIdCamel = node.get("productId");
            if (productIdCamel != null && productIdCamel.isIntegralNumber()) {
                productIds.add(productIdCamel.asLong());
            }
            node.elements().forEachRemaining(child -> collectProductIds(child, productIds));
            return;
        }

        if (node.isArray()) {
            node.forEach(child -> collectProductIds(child, productIds));
        }
    }
}

