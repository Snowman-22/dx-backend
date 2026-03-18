package com.snowman.team2.domain.chat.dto;

import java.util.Map;

/**
 * Spring → 프론트(STOMP) 응답 DTO.
 */
public record ChatReply(
        String convId,
        String aiResponse,
        Map<String, Object> state
) {
}

