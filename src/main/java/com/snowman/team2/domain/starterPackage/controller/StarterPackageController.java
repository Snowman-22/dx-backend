package com.snowman.team2.domain.starterPackage.controller;

import com.snowman.team2.domain.starterPackage.dto.StarterPackageRequest;
import com.snowman.team2.domain.starterPackage.dto.StarterPackageResponse;
import com.snowman.team2.domain.starterPackage.service.StarterPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/starter-package")
public class StarterPackageController {

    private final StarterPackageService starterPackageService;

    /**
     * 스타터 타입으로 추천 상품 목록 조회
     * Request: { "guest_sessionId": "...", "starter_type": "MOVE_REMODEL" }
     * Response: { "guest_sessionId", "starter_type", "items": [{ rank, product_name, ... }] }
     */
    @PostMapping("/recommend")
    public ResponseEntity<StarterPackageResponse> getRecommendation(
            @Valid @RequestBody StarterPackageRequest request) {
        StarterPackageResponse response = starterPackageService.getRecommendation(request);
        return ResponseEntity.ok(response);
    }
}
