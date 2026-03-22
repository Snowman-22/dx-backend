package com.snowman.team2.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * FastAPI → Spring 응답 DTO (POST /ai/chat).
 *
 * 스펙:
 * - data: 저장/추천 결과 등 payload
 * - aiResponse: 자유 대화 응답(또는 null) — FastAPI는 camelCase로 내려줄 수 있음
 */
public record FastApiChatResponse(
        Map<String, Object> data,
        @JsonProperty("aiResponse")
        @JsonAlias({"ai_response"})
        String aiResponse
) {
}

