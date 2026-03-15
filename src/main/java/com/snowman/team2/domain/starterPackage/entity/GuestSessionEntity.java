package com.snowman.team2.domain.starterPackage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 비로그인 사용자의 세션 정보.
 * 스타터 패키지 선택 후 회원가입 시, sessionId로 조회해 선택한 starter_type을 넘겨주기 위해 저장.
 */
@Entity
@Table(name = "guest_session")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestSessionEntity {

    @Id
    @Column(name = "session_id", nullable = false, length = 100)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "starter_type", nullable = false)
    private StarterPackageType starterType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void setTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }
}
