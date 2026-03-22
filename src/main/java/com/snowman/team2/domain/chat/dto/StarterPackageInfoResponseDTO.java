package com.snowman.team2.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 채팅에 연결된 스타터 패키지 조회 응답.
 */
public record StarterPackageInfoResponseDTO(
        @JsonProperty("starter_package_id")
        Long starterPackageId,

        @JsonProperty("starter_package_name")
        String starterPackageName,

        String description
) {
}
