package com.snowman.team2.domain.chat.dto.request;

import java.util.Map;

/**
 * 프론트 → Spring(STOMP) 요청 DTO.
 */
public record ChatMessage(
        String sessionId,
        String content,
        Map<String, Object> state
) {
}

