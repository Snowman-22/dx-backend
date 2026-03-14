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
    private Long starter_package_id;

    @Column(name = "starter_package_name", nullable = false)
    private String starter_package_name;

    @Column(name = "is_use", nullable = false)
    @Builder.Default
    private Boolean is_use = true;
}
