package com.snowman.team2.domain.starterPackage.repository;

import com.snowman.team2.domain.starterPackage.entity.StarterPackageItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StarterPackageItemRepository extends JpaRepository<StarterPackageItemEntity, Long> {

    List<StarterPackageItemEntity> findByStarterPackage_StarterPackageIdOrderByRankAsc(Long starterPackageId);
}
