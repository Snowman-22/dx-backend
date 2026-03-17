package com.snowman.team2.domain.starterPackage.entity;

import com.snowman.team2.domain.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void assignUser(UserEntity user) {
        this.user = user;
    }

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
