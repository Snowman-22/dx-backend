package com.snowman.team2.domain.cart.controller;

import com.snowman.team2.domain.cart.dto.response.CartItemResponseDTO;
import com.snowman.team2.domain.cart.service.CartService;
import com.snowman.team2.domain.reco.dto.request.SelectRecommendationRequestDTO;
import com.snowman.team2.domain.reco.service.RecommendationService;
import com.snowman.team2.global.userDetails.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final RecommendationService recommendationService;

    /**
     * 해당 채팅의 추천 패키지(recommendation)를 선택해 장바구니에 상품을 담는다.
     * path의 chatId는 chat_conv_id 문자열.
     */
    @PostMapping("/{chatId}/select")
    public ResponseEntity<Void> selectRecommendation(
            @PathVariable String chatId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SelectRecommendationRequestDTO request
    ) {
        recommendationService.selectRecommendation(chatId, request, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<List<CartItemResponseDTO>> getCartItems(
            @PathVariable String chatId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(cartService.getCartItems(chatId, userDetails.getUserId()));
    }

    @DeleteMapping("/{chatId}/recommendations/{recommendationId}")
    public ResponseEntity<Void> deleteRecommendationCartItems(
            @PathVariable String chatId,
            @PathVariable Long recommendationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        cartService.deleteRecommendationCartItems(chatId, recommendationId, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }
}
