package com.snowman.team2.domain.chat.dto;

/**
 * 프론트 → Spring(STOMP) 요청 DTO.
 *
 * convId: LangGraph/FastAPI에서 사용하는 세션 식별자
 * stepCode: CHAT_0 ~ RAG_CHAT 단계 코드
 * assistantText: 이번 턴에 화면에 노출된 고정 안내 문구(없으면 null)
 * userText: 사용자가 실제로 입력/선택한 값
 */
public record ChatMessage(
        String convId,
        String stepCode,
        String assistantText,
        Object userText
) {
}

