package com.snowman.team2.domain.starterPackage.entity;

import com.snowman.team2.domain.product.entity.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "starter_package_item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StarterPackageItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "starter_package_item_id", nullable = false)
    private Long starterPackageItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "starter_package_id", nullable = false)
    private StarterPackageEntity starterPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "rank", nullable = false)
    private Integer rank;
}
