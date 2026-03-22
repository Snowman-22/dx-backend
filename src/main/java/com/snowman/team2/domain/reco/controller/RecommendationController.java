package com.snowman.team2.domain.reco.controller;

import com.snowman.team2.global.userDetails.CustomUserDetails;
import com.snowman.team2.domain.reco.dto.response.RecommendationDTO;
import com.snowman.team2.domain.reco.dto.response.RecommendationsPageResponseDTO;
import com.snowman.team2.domain.reco.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * 해당 채팅의 추천 패키지 전체 목록.
     */
    @GetMapping("/{chatId}")
    public ResponseEntity<List<RecommendationDTO>> getRecommendations(
            @PathVariable String chatId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(recommendationService.getRecommendations(chatId, userDetails.getUserId()));
    }

    /**
     * 추천 패키지 페이지 조회 (한 페이지 3개). 다음 페이지가 없으면 {@code DATA_NOT_EXIST} 예외.
     *
     * @param page 1부터 시작 (1=첫 3개, 2=다음 3개, …)
     */
    @GetMapping("/{chatId}/page")
    public ResponseEntity<RecommendationsPageResponseDTO> getRecommendationsPage(
            @PathVariable String chatId,
            @RequestParam(defaultValue = "1") int page,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                recommendationService.getRecommendationsPage(chatId, page, userDetails.getUserId())
        );
    }
}

