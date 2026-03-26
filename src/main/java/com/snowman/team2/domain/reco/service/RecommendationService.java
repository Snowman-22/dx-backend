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
import com.snowman.team2.domain.reco.dto.response.RecommendationsPageResponseDTO;
import com.snowman.team2.domain.reco.entity.RecommendationEntity;
import com.snowman.team2.domain.reco.repository.RecommendationRepository;
import com.snowman.team2.global.exception.ErrorCode;
import com.snowman.team2.global.exception.exceptionType.BadRequestException;
import com.snowman.team2.global.exception.exceptionType.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    /** {@link #getRecommendationsPage(String, int, Long)} 한 페이지당 패키지 수 */
    public static final int RECOMMENDATION_PAGE_SIZE = 3;

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

        List<RecommendationEntity> recs =
                recommendationRepository.findAllByChat_ChatIdOrderByRecommendationIdAsc(chat.getChatId());
        return recs.stream()
                .map(r -> new RecommendationDTO(
                        r.getRecommendationId(),
                        chat.getChatConvId(),
                        r.getPackageName(),
                        r.getReason(),
                        r.getRecommendationPlus(),
                        r.getProducts(),
                        r.getIsSelected()
                ))
                .toList();
    }

    /**
     * 추천 패키지를 페이지 단위(기본 3개)로 조회한다.
     * 요청한 페이지에 더 이상 항목이 없으면 예외를 던진다. (빈 목록은 page=1일 때만)
     * page는 1부터 시작 (1=첫 3개, 2=다음 3개, …).
     */
    @Transactional(readOnly = true)
    public RecommendationsPageResponseDTO getRecommendationsPage(String chatId, int page, Long userId) {
        if (page < 1) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "page는 1 이상이어야 합니다.");
        }

        ChatEntity chat = chatRepository.findByChatConvId(chatId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "채팅을 찾을 수 없습니다."));

        checkChatOwner(chat, userId);

        List<RecommendationEntity> all =
                recommendationRepository.findAllByChat_ChatIdOrderByRecommendationIdAsc(chat.getChatId());
        List<RecommendationEntity> arranged = arrangeRecommendationsForPaging(all);
        int total = arranged.size();
        int size = RECOMMENDATION_PAGE_SIZE;
        int start = (page - 1) * size;

        if (total == 0 && page == 1) {
            return new RecommendationsPageResponseDTO(List.of(), 1, size, 0, false);
        }
        if (start >= total) {
            throw new BadRequestException(ErrorCode.DATA_NOT_EXIST, "더 이상 추천 패키지가 없습니다.");
        }

        int end = Math.min(start + size, total);
        List<RecommendationDTO> slice = arranged.subList(start, end).stream()
                .map(r -> new RecommendationDTO(
                        r.getRecommendationId(),
                        chat.getChatConvId(),
                        r.getPackageName(),
                        r.getReason(),
                        r.getRecommendationPlus(),
                        r.getProducts(),
                        r.getIsSelected()
                ))
                .toList();

        boolean hasNext = end < total;
        return new RecommendationsPageResponseDTO(slice, page, size, total, hasNext);
    }

    /**
     * package_name별로 추천을 묶은 뒤, 각 그룹에서 1개씩 번갈아 꺼내도록 재배열한다.
     * 이렇게 하면 한 페이지 안에 같은 package_name이 최대한 덜 겹치게 된다.
     */
    private List<RecommendationEntity> arrangeRecommendationsForPaging(List<RecommendationEntity> recommendations) {
        Map<String, List<RecommendationEntity>> grouped = new LinkedHashMap<>();
        for (RecommendationEntity recommendation : recommendations) {
            String packageName = recommendation.getPackageName();
            grouped.computeIfAbsent(packageName, key -> new ArrayList<>()).add(recommendation);
        }

        List<RecommendationEntity> arranged = new ArrayList<>(recommendations.size());
        boolean added;
        int roundIndex = 0;

        do {
            added = false;
            for (List<RecommendationEntity> group : grouped.values()) {
                if (roundIndex < group.size()) {
                    arranged.add(group.get(roundIndex));
                    added = true;
                }
            }
            roundIndex++;
        } while (added);

        return arranged;
    }

    /**
     * 선택된 recommendation의 제품 목록을 사용자 cart에 담는다.
     */
    @Transactional
    public void selectRecommendation(String chatId, SelectRecommendationRequestDTO request, Long userId) {
        RecommendationEntity recommendation = chooseRecommendation(chatId, request, userId);

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
            var existingOpt = cartRepository.findByUser_UserIdAndChatConvIdAndRecommendationIdAndProduct_ProductIdAndIsDeleteFalse(
                    userId,
                    chatId,
                    recommendation.getRecommendationId(),
                    productId
            );
            if (existingOpt.isPresent()) {
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
                    .chatConvId(chatId)
                    .recommendationId(recommendation.getRecommendationId())
                    .build());
            addedCount++;
        }

        if (addedCount == 0) {
            throw new BadRequestException(ErrorCode.DATA_ALREADY_EXIST, "이미 선택하여 카트에 담긴 추천입니다.");
        }
    }

    /**
     * 채팅 내 추천 패키지 하나를 선택 상태로 변경한다.
     */
    @Transactional
    public RecommendationEntity chooseRecommendation(String chatId, SelectRecommendationRequestDTO request, Long userId) {
        ChatEntity chat = chatRepository.findByChatConvId(chatId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "채팅을 찾을 수 없습니다."));

        checkChatOwner(chat, userId);

        RecommendationEntity recommendation = recommendationRepository
                .findByChat_ChatIdAndRecommendationId(chat.getChatId(), request.recommendationId())
                .orElseThrow(() -> new BadRequestException(
                        ErrorCode.DATA_NOT_EXIST,
                        "지정한 recommendation이 해당 chat에 존재하지 않습니다."
                ));

        recommendationRepository.resetSelectedByChatId(chat.getChatId());
        recommendationRepository.selectByChatIdAndRecommendationId(chat.getChatId(), recommendation.getRecommendationId());

        return recommendationRepository
                .findByChat_ChatIdAndRecommendationId(chat.getChatId(), recommendation.getRecommendationId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "선택한 recommendation을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<Long> getProductIdsForRecommendation(String chatId, Long recommendationId, Long userId) {
        RecommendationEntity recommendation = getRecommendation(chatId, recommendationId, userId);
        return extractProductIds(recommendation.getProducts());
    }

    @Transactional(readOnly = true)
    public RecommendationEntity getRecommendation(String chatId, Long recommendationId, Long userId) {
        ChatEntity chat = chatRepository.findByChatConvId(chatId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "채팅을 찾을 수 없습니다."));

        checkChatOwner(chat, userId);

        return recommendationRepository
                .findByChat_ChatIdAndRecommendationId(chat.getChatId(), recommendationId)
                .orElseThrow(() -> new BadRequestException(
                        ErrorCode.DATA_NOT_EXIST,
                        "지정한 recommendation이 해당 chat에 존재하지 않습니다."
                ));
    }

    @Transactional
    public Map<String, Object> saveAndAttachRecommendationIds(Long chatId, String chatConvId, Map<String, Object> fastApiData) {
        if (fastApiData == null) {
            return Map.of();
        }

        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "채팅을 찾을 수 없습니다."));

        Object recommendationsRaw = fastApiData.get("recommendations");
        if (recommendationsRaw == null) {
            recommendationsRaw = fastApiData.get("all_recommendations");
        }
        if (recommendationsRaw == null) {
            recommendationsRaw = fastApiData.get("recommendation_list");
        }
        if (!(recommendationsRaw instanceof List<?> recommendations)) {
            // 추천 리스트가 없는 응답은 그대로 전달
            return fastApiData;
        }

        // 최신 추천 결과로 교체한다.
        recommendationRepository.deleteAllByChat_ChatId(chatId);

        List<RecommendationEntity> entitiesToSave = new ArrayList<>();
        List<Map<String, Object>> normalizedRecommendations = new ArrayList<>();
        int maxRecommendationCount = 12;
        for (Object item : recommendations) {
            if (entitiesToSave.size() >= maxRecommendationCount) {
                break;
            }
            if (!(item instanceof Map<?, ?> mapItem)) {
                continue;
            }

            String reason = toNullableString(firstNonNull(mapItem.get("reason"), mapItem.get("recommendationReason")));
            String packageName = toNullableString(firstNonNull(mapItem.get("package_name"), mapItem.get("packageName")));
            String recommendationPlus = toNullableString(firstNonNull(
                    mapItem.get("recommendation_plus"),
                    mapItem.get("recommendationPlus")
            ));
            Object productsObject = mapItem.get("products");
            if (productsObject == null) {
                Map<String, Object> groupedProducts = new LinkedHashMap<>();
                if (mapItem.get("appliances") != null) {
                    groupedProducts.put("appliances", mapItem.get("appliances"));
                }
                if (mapItem.get("furniture") != null) {
                    groupedProducts.put("furniture", mapItem.get("furniture"));
                }
                productsObject = groupedProducts.isEmpty() ? null : groupedProducts;
            }
            String products = toJsonString(productsObject);

            entitiesToSave.add(RecommendationEntity.builder()
                    .chat(chat)
                    .isSelected(false)
                    .reason(reason)
                    .packageName(packageName)
                    .recommendationPlus(recommendationPlus)
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
            if (savedEntity.getRecommendationPlus() != null && !row.containsKey("recommendation_plus")) {
                row.put("recommendation_plus", savedEntity.getRecommendationPlus());
            }
            withIds.add(row);
        }

        Map<String, Object> responseData = new LinkedHashMap<>(fastApiData);
        responseData.put("recommendations", withIds);
        if (fastApiData.containsKey("all_recommendations")) {
            responseData.put("all_recommendations", withIds);
        }
        if (fastApiData.containsKey("recommendation_list")) {
            responseData.put("recommendation_list", withIds);
        }
        return responseData;
    }

    private Object firstNonNull(Object first, Object second) {
        return first != null ? first : second;
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
            Set<Long> productIds = new LinkedHashSet<>();
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

        if (node.isObject()) {
            JsonNode productIdNode = node.get("product_id");
            if (productIdNode != null) {
                addProductIdIfValid(productIdNode, productIds);
            }
            JsonNode productIdCamel = node.get("productId");
            if (productIdCamel != null) {
                addProductIdIfValid(productIdCamel, productIds);
            }
            node.elements().forEachRemaining(child -> collectProductIds(child, productIds));
            return;
        }

        if (node.isArray()) {
            node.forEach(child -> collectProductIds(child, productIds));
        }
    }

    private void addProductIdIfValid(JsonNode productIdNode, Set<Long> productIds) {
        if (productIdNode == null || productIdNode.isNull()) {
            return;
        }
        if (productIdNode.isIntegralNumber()) {
            productIds.add(productIdNode.asLong());
            return;
        }
        if (productIdNode.isTextual()) {
            try {
                productIds.add(Long.parseLong(productIdNode.asText()));
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
