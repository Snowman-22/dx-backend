package com.snowman.team2.domain.chat.repository;

import com.snowman.team2.domain.chat.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    Optional<ChatEntity> findByChatConvId(String chatConvId);
}

