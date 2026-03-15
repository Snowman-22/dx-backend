package com.snowman.team2.domain.starterPackage.dto.response;

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
public class StarterPackageResponseDTO {

    @JsonProperty("guest_sessionId")
    private String guestSessionId;

    @JsonProperty("starter_type")
    private StarterPackageType starterType;

    @JsonProperty("items")
    private List<StarterPackageItemResponseDTO> items;

    public static StarterPackageResponseDTO fromEntity(
            String guestSessionId,
            StarterPackageType starterType,
            List<StarterPackageItemEntity> items) {
        List<StarterPackageItemResponseDTO> itemResponses = items.stream()
                .map(StarterPackageItemResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return StarterPackageResponseDTO.builder()
                .guestSessionId(guestSessionId)
                .starterType(starterType)
                .items(itemResponses)
                .build();
    }
}
