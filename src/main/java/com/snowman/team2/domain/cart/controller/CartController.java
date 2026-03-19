package com.snowman.team2.domain.cart.controller;

import com.snowman.team2.domain.cart.service.CartService;
import com.snowman.team2.global.userDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable Long cartId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        cartService.deleteCartItem(cartId, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }
}
