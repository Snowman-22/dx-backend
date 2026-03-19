package com.snowman.team2.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record PrestartChatRequestDTO(
        @NotNull
        @JsonProperty("starter_package_id")
        Long starterPackageId
) {
}

