package com.snowman.team2.domain.simulation.entity;

import com.snowman.team2.domain.starterPackage.entity.GuestSessionEntity;
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
    private Long layout2dId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_session_id", nullable = false)
    private GuestSessionEntity guestSession;

    @Column(name = "layout_2d_img_url", nullable = false)
    private String layout2dImgUrl;
}
