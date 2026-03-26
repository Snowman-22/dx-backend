package com.snowman.team2.domain.reco.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecommendationDTO(
        @JsonProperty("recommendation_id")
        Long recommendationId,

        @JsonProperty("chat_uuid")
        String chatId,

        @JsonProperty("package_name")
        String packageName,

        String reason,

        @JsonProperty("recommendation_plus")
        String recommendationPlus,

        String products,

        @JsonProperty("is_selected")
        Boolean isSelected
) {
}
