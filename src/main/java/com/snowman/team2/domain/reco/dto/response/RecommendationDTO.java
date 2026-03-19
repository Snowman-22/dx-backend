package com.snowman.team2.domain.reco.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecommendationDTO(
        @JsonProperty("recommendation_id")
        Long recommendationId,

        @JsonProperty("chat_id")
        Long chatId,

        String reason,

        String products,

        @JsonProperty("is_selected")
        Boolean isSelected
) {
}

