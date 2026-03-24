package com.snowman.team2.domain.cart.service;

import com.snowman.team2.domain.cart.dto.response.CartItemResponseDTO;
import com.snowman.team2.domain.cart.entity.CartEntity;
import com.snowman.team2.domain.cart.repository.CartRepository;
import com.snowman.team2.domain.chat.entity.ChatEntity;
import com.snowman.team2.domain.chat.repository.ChatRepository;
import com.snowman.team2.global.exception.ErrorCode;
import com.snowman.team2.global.exception.exceptionType.BadRequestException;
import com.snowman.team2.global.exception.exceptionType.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ChatRepository chatRepository;

    private void checkChatOwner(ChatEntity chat, Long userId) {
        if (chat.getUser() == null
                || chat.getUser().getUserId() == null
                || !chat.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedException(ErrorCode.ACCESS_DENIED, "접근 권한이 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<CartItemResponseDTO> getCartItems(String chatConvId, Long userId) {
        return cartRepository.findAllByUser_UserIdAndChatConvIdAndIsDeleteFalseOrderByCreateDateDesc(userId, chatConvId).stream()
                .map(cart -> new CartItemResponseDTO(
                        cart.getCartId(),
                        cart.getProduct().getProductId(),
                        cart.getProduct().getModelId(),
                        cart.getProduct().getProductName(),
                        cart.getProduct().getBrand(),
                        cart.getProduct().getCategory(),
                        cart.getQuantity(),
                        cart.getProduct().getDiscountPrice(),
                        cart.getProduct().getProductImageUrl(),
                        cart.getProduct().getProductUrl()
                ))
                .toList();
    }

    @Transactional
    public void deleteRecommendationCartItems(String chatConvId, Long recommendationId, Long userId) {
        ChatEntity chat = chatRepository.findByChatConvId(chatConvId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "채팅을 찾을 수 없습니다."));

        checkChatOwner(chat, userId);

        List<CartEntity> carts = cartRepository
                .findAllByUser_UserIdAndChatConvIdAndRecommendationIdAndIsDeleteFalseOrderByCreateDateDesc(
                        userId,
                        chatConvId,
                        recommendationId
                );

        if (carts.isEmpty()) {
            throw new BadRequestException(ErrorCode.DATA_NOT_EXIST, "삭제할 추천 패키지 카트 항목을 찾을 수 없습니다.");
        }

        carts.forEach(CartEntity::markDeleted);
    }
}
