package com.snowman.team2.domain.chat.entity;

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
    private Long chatId;

    // 프론트/웹소켓에서 쓰는 외부 식별자(숫자 대신 복잡한 문자열)
    @Column(name = "chat_conv_id", nullable = true, unique = true)
    private String chatConvId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "starter_package_id", nullable = false)
    private StarterPackageEntity starterPackage;

    @Column(name = "chat_title")
    private String chatTitle;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "is_select_blueprint", nullable = false)
    @Builder.Default
    private Boolean isSelectBlueprint = false;

    public void assignUser(UserEntity user) {
        this.user = user;
    }

    public void updateChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }
}
