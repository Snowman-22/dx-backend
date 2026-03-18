package com.snowman.team2.domain.chat.dto.response;

import java.util.Map;

/**
 * FastAPI → Spring 응답 DTO.
 */
public record FastApiChatResponse(
        String session_id,
        String ai_response,
        Map<String, Object> state
) {
}

