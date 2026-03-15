package com.snowman.team2.domain.starterPackage.repository;

import com.snowman.team2.domain.starterPackage.entity.GuestSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestSessionRepository extends JpaRepository<GuestSessionEntity, String> {
}
