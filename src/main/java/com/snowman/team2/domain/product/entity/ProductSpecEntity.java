package com.snowman.team2.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Product_spec")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSpecEntity {

    @Id
    @Column(name = "product_id", nullable = false)
    private Long product_id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(name = "width")
    private Float width;

    @Column(name = "height")
    private Float height;

    @Column(name = "depth")
    private Float depth;

    @Column(name = "weight")
    private Float weight;
}
