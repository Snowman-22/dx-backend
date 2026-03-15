package com.snowman.team2.domain.starterPackage.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.snowman.team2.domain.starterPackage.entity.GuestSessionEntity;
import com.snowman.team2.domain.starterPackage.entity.StarterPackageType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StarterPackageRequestDTO {

    @NotNull(message = "starter_type은 필수입니다")
    @JsonProperty("starter_type")
    private StarterPackageType starterType;

    public GuestSessionEntity toEntity(String sessionId) {
        return GuestSessionEntity.builder()
                .sessionId(sessionId)
                .starterType(this.starterType)
                .build();
    }
}
