package com.snowman.team2.domain.starterPackage.repository;

import com.snowman.team2.domain.starterPackage.entity.StarterPackageEntity;
import com.snowman.team2.domain.starterPackage.entity.StarterPackageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StarterPackageRepository extends JpaRepository<StarterPackageEntity, Long> {

    Optional<StarterPackageEntity> findByStarterPackageNameAndIsUseTrue(StarterPackageType starterPackageName);
}
