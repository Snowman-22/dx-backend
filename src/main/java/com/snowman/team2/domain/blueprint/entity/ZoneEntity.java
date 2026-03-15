package com.snowman.team2.domain.blueprint.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Zone")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zone_id", nullable = false)
    private Long zoneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blueprint_id", nullable = false)
    private BlueprintEntity blueprint;

    @Column(name = "zone_type", nullable = false)
    private String zoneType;

    @Column(name = "width", nullable = false)
    private Float width;

    @Column(name = "height", nullable = false)
    private Float height;
}
