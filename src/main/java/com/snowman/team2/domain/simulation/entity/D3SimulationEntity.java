package com.snowman.team2.domain.simulation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "D3_simulation")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class D3SimulationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "layout_3d_id", nullable = false)
    private Long layout3dId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "layout_2d_id", nullable = false)
    private D2SimulationEntity d2Simulation;

    @Column(name = "layout_3d_img_url", nullable = false)
    private String layout3dImgUrl;
}
