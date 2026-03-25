package com.snowman.team2.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record ConversationHistoryResponseDTO(
        @JsonProperty("conversation")
        Map<String, Object> conversation
) {
}
