package com.snowman.team2.domain.chat.entity;

import com.snowman.team2.domain.blueprint.entity.BlueprintEntity;
import com.snowman.team2.domain.auth.entity.UserEntity;
import com.snowman.team2.domain.starterPackage.entity.StarterPackageEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Chat")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id", nullable = false)
    private Long chat_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "starter_package_id", nullable = false)
    private StarterPackageEntity starter_package;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blueprint_id")
    private BlueprintEntity blueprint;

    @Column(name = "chat_title")
    private String chat_title;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start_date;

    @Column(name = "end_date")
    private LocalDateTime end_date;

    @Column(name = "is_select_blueprint", nullable = false)
    @Builder.Default
    private Boolean is_select_blueprint = false;
}
