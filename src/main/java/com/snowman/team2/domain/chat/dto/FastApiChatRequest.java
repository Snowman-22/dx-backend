package com.snowman.team2.domain.chat.dto;

/**
 * Spring → FastAPI 요청 DTO.
 *
 * FastAPI/LangGraph 쪽 요구사항:
 * - conv_id: 대화 세션 ID
 * - step_code: CHAT_0 ~ RAG_CHAT 단계 코드
 * - assistant_text: 이번 턴에 노출된 assistant 문구(없으면 null)
 * - user_text: 사용자의 입력
 */
public record FastApiChatRequest(
        String conv_id,
        String step_code,
        String assistant_text,
        Object user_text
) {
}

