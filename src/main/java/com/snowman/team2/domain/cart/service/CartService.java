package com.snowman.team2.domain.cart.service;

import com.snowman.team2.domain.cart.dto.response.CartItemResponseDTO;
import com.snowman.team2.domain.cart.entity.CartEntity;
import com.snowman.team2.domain.cart.repository.CartRepository;
import com.snowman.team2.domain.reco.service.RecommendationService;
import com.snowman.team2.global.exception.ErrorCode;
import com.snowman.team2.global.exception.exceptionType.BadRequestException;
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
    private final RecommendationService recommendationService;

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

    /**
     * 특정 chat + 특정 recommendation을 선택해서 담긴 cart items 조회.
     * recommendation 원본의 product 목록과 현재 cart 상태를 교집합으로 계산한다.
     */
    @Transactional(readOnly = true)
    public List<CartItemResponseDTO> getCartItemsBySelectedRecommendation(String chatConvId, Long recommendationId, Long userId) {
        List<Long> productIds = recommendationService.getProductIdsForRecommendation(chatConvId, recommendationId, userId);
        if (productIds.isEmpty()) {
            return List.of();
        }
        return cartRepository
                .findAllByUser_UserIdAndChatConvIdAndIsDeleteFalseAndProduct_ProductIdInOrderByCreateDateDesc(
                        userId,
                        chatConvId,
                        productIds
                )
                .stream()
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
    public void deleteCartItem(Long cartId, Long userId) {
        CartEntity cart = cartRepository.findByCartIdAndUser_UserIdAndIsDeleteFalse(cartId, userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "삭제할 카트 항목을 찾을 수 없습니다."));
        cart.markDeleted();
    }
}
