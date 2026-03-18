package com.snowman.team2.domain.chat.dto;

import java.util.Map;

/**
 * FastAPI → Spring 응답 DTO.
 *
 * FastAPI 쪽에서 conv_id, ai_response 정도만 내려준다고 가정.
 * state 등 추가 정보가 있다면 여기 필드를 확장하면 된다.
 */
public record FastApiChatResponse(
        String conv_id,
        String ai_response,
        Map<String, Object> state
) {
}

