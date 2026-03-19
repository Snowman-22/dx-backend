package com.snowman.team2.domain.chat.dto;

import java.util.Map;

/**
 * Spring → 프론트(STOMP) 응답 DTO.
 *
 * data: FastAPI가 내려준 payload 그대로 전달
 * aiResponse: 필요 시 추가 응답 텍스트 (없으면 null)
 */
public record ChatReply(
        String convId,
        Map<String, Object> data,
        String aiResponse
) {
}

