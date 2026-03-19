package com.snowman.team2.domain.reco.entity;

import com.snowman.team2.domain.chat.entity.ChatEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Recommendation")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id", nullable = false)
    private Long recommendationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chat;

    @Column(name = "is_selected", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isSelected = false;

    @Column(name = "reason")
    private String reason;

    @Column(name = "products")
    private String products;  // JSON 또는 직렬화된 product ID 목록
}
