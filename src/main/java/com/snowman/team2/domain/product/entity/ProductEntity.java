package com.snowman.team2.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Product")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "category")
    private String category;

    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "brand")
    private String brand;

    @Column(name = "original_price")
    private Float originalPrice;

    @Column(name = "discount_rate")
    private Float discountRate;

    @Column(name = "discount_price")
    private Float discountPrice;

    @Column(name = "is_subscribe")
    private Boolean isSubscribe;

    @Column(name = "review_score")
    private Float reviewScore;

    @Column(name = "review_cnt")
    private Integer reviewCnt;

    @Column(name = "product_url")
    private String productUrl;

    @Column(name = "product_image_url")
    private String productImageUrl;
}
