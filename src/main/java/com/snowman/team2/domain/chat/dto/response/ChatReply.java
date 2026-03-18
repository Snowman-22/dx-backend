package com.snowman.team2.domain.chat.dto.response;

import java.util.Map;

/**
 * Spring → 프론트(STOMP) 응답 DTO.
 */
public record ChatReply(
        String sessionId,
        String aiResponse,
        Map<String, Object> state
) {
}

