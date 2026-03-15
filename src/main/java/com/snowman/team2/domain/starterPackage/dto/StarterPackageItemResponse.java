package com.snowman.team2.domain.starterPackage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.snowman.team2.domain.starterPackage.entity.StarterPackageItemEntity;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StarterPackageItemResponse {

    @JsonProperty("rank")
    private Integer rank;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_description")
    private String productDescription;

    @JsonProperty("product_imageUrl")
    private String productImageUrl;

    @JsonProperty("price")
    private Long price;

    /** StarterPackageItemEntity → DTO */
    public static StarterPackageItemResponse fromEntity(StarterPackageItemEntity item) {
        var product = item.getProduct();
        Long price = product.getDiscountPrice() != null
                ? product.getDiscountPrice().longValue()
                : (product.getOriginalPrice() != null ? product.getOriginalPrice().longValue() : 0L);

        return StarterPackageItemResponse.builder()
                .rank(item.getRank())
                .productName(product.getProductName())
                .productDescription(product.getProductDescription())
                .productImageUrl(product.getProductImageUrl())
                .price(price)
                .build();
    }
}
