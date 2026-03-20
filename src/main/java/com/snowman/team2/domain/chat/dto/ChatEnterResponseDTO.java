package com.snowman.team2.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.snowman.team2.domain.starterPackage.entity.StarterPackageType;

public record ChatEnterResponseDTO(
        @JsonProperty("chat_uuid")
        String chatId,

        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("user_name")
        String userName,

        @JsonProperty("starter_package_id")
        Long starterPackageId,

        @JsonProperty("starter_package_type")
        StarterPackageType starterPackageType,

        @JsonProperty("starter_package_description")
        String starterPackageDescription
) {
}

