package com.snowman.team2.domain.starterPackage.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.snowman.team2.domain.starterPackage.entity.StarterPackageType;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StarterPackageResponseDTO {

    @JsonProperty("guest_session")
    private String guestSession;

    @JsonProperty("starter_type")
    private StarterPackageType starterType;

    public static StarterPackageResponseDTO fromEntity(
            String guestSession,
            StarterPackageType starterType) {
        return StarterPackageResponseDTO.builder()
                .guestSession(guestSession)
                .starterType(starterType)
                .build();
    }
}
