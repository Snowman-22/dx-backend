package com.snowman.team2.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Subscribe_price")
@IdClass(SubscribePriceId.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscribePriceEntity {

    @Id
    @Column(name = "subscribe_id", nullable = false)
    private Long subscribeId;

    @Id
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private ProductEntity product;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "price", nullable = false)
    private Float price;

    @Column(name = "contract_period_year")
    private Integer contractPeriodYear;

    @Column(name = "mandatory_period_year")
    private Integer mandatoryPeriodYear;

    @Column(name = "visit_service_type")
    private String visitServiceType;

    @Column(name = "visit_cycle_month")
    private Integer visitCycleMonth;
}
