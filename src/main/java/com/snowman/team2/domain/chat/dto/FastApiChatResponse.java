package com.snowman.team2.domain.chat.dto;

import java.util.Map;

/**
 * FastAPI → Spring 응답 DTO.
 *
 * 스펙:
 * - data: 저장/추천 결과 등 payload
 * - ai_response: 자유 대화 응답(또는 null)
 */
public record FastApiChatResponse(
        Map<String, Object> data,
        String ai_response
) {
}

