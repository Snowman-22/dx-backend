package com.snowman.team2.domain.blueprint.entity;

import com.snowman.team2.domain.starterPackage.entity.GuestSessionEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Blueprint")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlueprintEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blueprint_id", nullable = false)
    private Long blueprintId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_session_id", nullable = false)
    private GuestSessionEntity guestSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private ZoneEntity zone;

    @Column(name = "blueprint_title", nullable = false)
    private String blueprintTitle;

    @Column(name = "blueprint_image_url")
    private String blueprintImageUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "square_footage", nullable = false)
    private Float squareFootage;

    @Column(name = "area_n2")
    private Float areaN2;

    @Column(name = "ceiling_height")
    private Float ceilingHeight;

    @Column(name = "width")
    private Float width;

    @Column(name = "depth")
    private Float depth;

    @Column(name = "room_count")
    private Integer roomCount;
}
