package com.snowman.team2.domain.starterPackage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.snowman.team2.domain.starterPackage.entity.StarterPackageItemEntity;
import com.snowman.team2.domain.starterPackage.entity.StarterPackageType;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StarterPackageResponse {

    @JsonProperty("guest_sessionId")
    private String guestSessionId;

    @JsonProperty("starter_type")
    private StarterPackageType starterType;

    @JsonProperty("items")
    private List<StarterPackageItemResponse> items;

    /** guestSessionId + starterType + item 엔티티 목록으로 Response 생성 */
    public static StarterPackageResponse fromEntity(
            String guestSessionId,
            StarterPackageType starterType,
            List<StarterPackageItemEntity> items) {
        List<StarterPackageItemResponse> itemResponses = items.stream()
                .map(StarterPackageItemResponse::fromEntity)
                .collect(Collectors.toList());

        return StarterPackageResponse.builder()
                .guestSessionId(guestSessionId)
                .starterType(starterType)
                .items(itemResponses)
                .build();
    }
}
