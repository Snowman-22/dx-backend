package com.snowman.team2.domain.chat.dto;

/**
 * Spring → FastAPI 요청 DTO (POST /ai/chat).
 * JSON은 snake_case로 전송하며, FastAPI에서 camelCase(convId 등)와 동일 동작이면 그에 맞춰도 됨.
 *
 * - conv_id: 대화 세션 ID (= chat_uuid)
 * - step_code: CHAT_0~CHAT_6, RECOMMEND_RAG, BLUEPRINT_RAG 등
 * - assistant_text: 화면 안내 문구(선택)
 * - user_text: 사용자 입력 — 문자열 | 배열 | 객체
 */
public record FastApiChatRequest(
        String conv_id,
        String step_code,
        String assistant_text,
        Object user_text
) {
}

