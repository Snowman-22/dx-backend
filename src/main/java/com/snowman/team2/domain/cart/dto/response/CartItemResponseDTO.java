package com.snowman.team2.domain.cart.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CartItemResponseDTO(
        @JsonProperty("cart_id")
        Long cartId,
        @JsonProperty("product_id")
        Long productId,
        @JsonProperty("model_id")
        String modelId,
        String name,
        String brand,
        String category,
        int quantity,
        @JsonProperty("price")
        Long price,
        @JsonProperty("image")
        String image,
        @JsonProperty("product_url")
        String productUrl
) {
}
