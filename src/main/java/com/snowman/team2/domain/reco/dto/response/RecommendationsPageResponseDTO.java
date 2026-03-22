package com.snowman.team2.domain.reco.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 추천 패키지 페이지 조회 (한 페이지당 3개).
 * {@code page}는 1부터 시작하는 현재 페이지 번호.
 */
public record RecommendationsPageResponseDTO(
        @JsonProperty("recommendations")
        List<RecommendationDTO> recommendations,

        /** 1-based 페이지 번호 */
        @JsonProperty("page")
        int page,

        @JsonProperty("page_size")
        int pageSize,

        @JsonProperty("total_count")
        long totalCount,

        @JsonProperty("has_next")
        boolean hasNext
) {
}
