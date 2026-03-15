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

    @Column(name = "starter_package_name", nullable = false)
    private String starterPackageName;

    @Column(name = "is_use", nullable = false)
    @Builder.Default
    private Boolean isUse = true;
}
