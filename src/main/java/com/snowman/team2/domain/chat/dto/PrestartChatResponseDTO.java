package com.snowman.team2.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record PrestartChatResponseDTO(
        @JsonProperty("chat_id")
        Long chatId,
        @JsonProperty("starter_package_id")
        Long starterPackageId,
        @JsonProperty("user_id")
        Long userId,
        String message
) {
}

