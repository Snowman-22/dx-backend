package com.snowman.team2.domain.reco.repository;
import com.snowman.team2.domain.reco.entity.RecommendationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<RecommendationEntity, Long> {

    List<RecommendationEntity> findAllByChat_ChatId(Long chatId);

    List<RecommendationEntity> findAllByChat_ChatIdOrderByRecommendationIdAsc(Long chatId);
    java.util.Optional<RecommendationEntity> findByChat_ChatIdAndRecommendationId(Long chatId, Long recommendationId);
    void deleteAllByChat_ChatId(Long chatId);

    @Modifying
    @Transactional
    @Query("""
            update RecommendationEntity r
            set r.isSelected = false
            where r.chat.chatId = :chatId
            """)
    int resetSelectedByChatId(@Param("chatId") Long chatId);

    @Modifying
    @Transactional
    @Query("""
            update RecommendationEntity r
            set r.isSelected = true
            where r.chat.chatId = :chatId
              and r.recommendationId = :recommendationId
            """)
    int selectByChatIdAndRecommendationId(
            @Param("chatId") Long chatId,
            @Param("recommendationId") Long recommendationId
    );
}

