package com.snowman.team2.domain.starterPackage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.snowman.team2.domain.starterPackage.entity.GuestSessionEntity;
import com.snowman.team2.domain.starterPackage.entity.StarterPackageType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StarterPackageRequest {

    /** 스타터 패키지 유형 (필수) */
    @NotNull(message = "starter_type은 필수입니다")
    @JsonProperty("starter_type")
    private StarterPackageType starterType;

    /** 발급된 세션 ID와 함께 GuestSession 엔티티로 변환 */
    public GuestSessionEntity toEntity(String sessionId) {
        return GuestSessionEntity.builder()
                .sessionId(sessionId)
                .starterType(this.starterType)
                .build();
    }
}
