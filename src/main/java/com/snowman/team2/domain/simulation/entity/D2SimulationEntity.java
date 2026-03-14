package com.snowman.team2.domain.simulation.entity;

import com.snowman.team2.domain.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "D2_simulation")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class D2SimulationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "layout_2d_id", nullable = false)
    private Long layout_2d_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "layout_2d_img_url", nullable = false)
    private String layout_2d_img_url;
}
