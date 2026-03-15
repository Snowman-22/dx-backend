package com.snowman.team2.domain.starterPackage.controller;

import com.snowman.team2.domain.starterPackage.dto.request.StarterPackageRequestDTO;
import com.snowman.team2.domain.starterPackage.dto.response.StarterPackageResponseDTO;
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

    @PostMapping("/recommend")
    public ResponseEntity<StarterPackageResponseDTO> getRecommendation(
            @Valid @RequestBody StarterPackageRequestDTO request) {
        StarterPackageResponseDTO response = starterPackageService.getRecommendation(request);
        return ResponseEntity.ok(response);
    }
}
