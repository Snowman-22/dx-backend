package com.snowman.team2.domain.chat.dto.request;

import java.util.Map;

/**
 * Spring → FastAPI 요청 DTO.
 */
public record FastApiChatRequest(
        String session_id,
        String last_user_input,
        Map<String, Object> state
) {
}

