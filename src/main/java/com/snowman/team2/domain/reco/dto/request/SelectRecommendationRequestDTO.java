package com.snowman.team2.domain.reco.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record SelectRecommendationRequestDTO(
        @NotNull
        @JsonProperty("recommendation_id")
        Long recommendationId
) {
}

