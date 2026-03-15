package com.snowman.team2.domain.starterPackage.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "StarterPackage")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StarterPackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "starter_package_id", nullable = false)
    private Long starterPackageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "starter_package_name")
    private StarterPackageType starterPackageName;

    @Column(name = "is_use")
    @Builder.Default
    private Boolean isUse = true;
}
