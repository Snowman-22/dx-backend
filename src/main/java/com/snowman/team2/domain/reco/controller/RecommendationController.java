package com.snowman.team2.domain.reco.controller;

import com.snowman.team2.global.userDetails.CustomUserDetails;
import com.snowman.team2.domain.reco.dto.request.SelectRecommendationRequestDTO;
import com.snowman.team2.domain.reco.dto.response.RecommendationDTO;
import com.snowman.team2.domain.reco.service.RecommendationService;
import jakarta.validation.Valid;
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

    @GetMapping("/{chatId}")
    public ResponseEntity<List<RecommendationDTO>> getRecommendations(
            @PathVariable Long chatId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(recommendationService.getRecommendations(chatId, userDetails.getUserId()));
    }

    /**
     * 선택 API:
     * - 같은 chat 내의 기존 selected 상태는 모두 false로 변경
     * - 선택된 recommendation row만 true로 변경
     */
    @PostMapping("/{chatId}/select")
    public ResponseEntity<Void> selectRecommendation(
            @PathVariable Long chatId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SelectRecommendationRequestDTO request
    ) {
        recommendationService.selectRecommendation(chatId, request, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }
}

