package com.snowman.team2.domain.simulation.entity;

import com.snowman.team2.domain.product.entity.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "D2_details")
@IdClass(D2DetailsId.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class D2DetailsEntity {

    @Id
    @Column(name = "d2_details_id", nullable = false)
    private Long d2_details_id;

    @Id
    @Column(name = "layout_2d_id", nullable = false)
    private Long layout_2d_id;

    @Id
    @Column(name = "product_id", nullable = false)
    private Long product_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "layout_2d_id", insertable = false, updatable = false)
    private D2SimulationEntity d2_simulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private ProductEntity product;

    @Column(name = "corr_x", nullable = false)
    private Float corr_x;

    @Column(name = "corr_y", nullable = false)
    private Float corr_y;

    @Column(name = "rotation")
    private Float rotation;
}
