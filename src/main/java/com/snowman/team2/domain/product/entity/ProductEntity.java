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
    private Long product_id;

    @Column(name = "product_name", nullable = false)
    private String product_name;

    @Column(name = "category")
    private String category;

    @Column(name = "product_category")
    private String product_category;

    @Column(name = "brand")
    private String brand;

    @Column(name = "original_price")
    private Float original_price;

    @Column(name = "discount_rate")
    private Float discount_rate;

    @Column(name = "discount_price")
    private Float discount_price;

    @Column(name = "is_subscribe")
    private Boolean is_subscribe;

    @Column(name = "review_score")
    private Float review_score;

    @Column(name = "review_cnt")
    private Integer review_cnt;

    @Column(name = "product_url")
    private String product_url;

    @Column(name = "product_image_url")
    private String product_image_url;
}
