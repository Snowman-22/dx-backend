package com.snowman.team2.domain.starterPackage.service;

import com.snowman.team2.domain.starterPackage.dto.request.StarterPackageRequestDTO;
import com.snowman.team2.domain.starterPackage.dto.response.StarterPackageResponseDTO;
import com.snowman.team2.domain.starterPackage.entity.GuestSessionEntity;
import com.snowman.team2.domain.starterPackage.entity.StarterPackageEntity;
import com.snowman.team2.domain.starterPackage.entity.StarterPackageItemEntity;
import com.snowman.team2.domain.starterPackage.entity.StarterPackageType;
import com.snowman.team2.domain.starterPackage.repository.GuestSessionRepository;
import com.snowman.team2.domain.starterPackage.repository.StarterPackageItemRepository;
import com.snowman.team2.domain.starterPackage.repository.StarterPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StarterPackageService {

    private final StarterPackageRepository starterPackageRepository;
    private final StarterPackageItemRepository starterPackageItemRepository;
    private final GuestSessionRepository guestSessionRepository;

    /**
     * 스타터 타입 선택 시: 서버가 guest_sessionId 발급 후 DB 저장, 해당 패키지 아이템 조회하여 응답.
     */
    @Transactional
    public StarterPackageResponseDTO getRecommendation(StarterPackageRequestDTO request) {
        String guestSessionId = generateAndSaveGuestSession(request);

        StarterPackageEntity starterPackage = starterPackageRepository
                .findByStarterPackageNameAndIsUseTrue(request.getStarterType())
                .orElseThrow(() -> new IllegalArgumentException("해당 스타터 패키지를 찾을 수 없습니다: " + request.getStarterType()));

        List<StarterPackageItemEntity> items = starterPackageItemRepository
                .findByStarterPackage_StarterPackageIdOrderByRankAsc(starterPackage.getStarterPackageId());

        return StarterPackageResponseDTO.fromEntity(guestSessionId, request.getStarterType(), items);
    }

    /** 세션 ID 생성 후 DB 저장 (회원가입 시 조회용) */
    private String generateAndSaveGuestSession(StarterPackageRequestDTO request) {
        String sessionId = "sess_" + UUID.randomUUID().toString().replace("-", "");
        GuestSessionEntity entity = request.toEntity(sessionId);
        guestSessionRepository.save(entity);
        return sessionId;
    }

    /**
     * 회원가입 시 사용. guest_sessionId로 저장된 스타터 타입 조회.
     * @return 선택한 스타터 타입 (없으면 empty)
     */
    @Transactional(readOnly = true)
    public Optional<StarterPackageType> getStarterTypeByGuestSessionId(String guestSessionId) {
        if (guestSessionId == null || guestSessionId.isBlank()) {
            return Optional.empty();
        }
        return guestSessionRepository.findById(guestSessionId)
                .map(GuestSessionEntity::getStarterType);
    }

    /**
     * 스타터 패키지 사용자 조회 (비로그인). guestSessionId로 저장된 스타터 타입 + 추천 상품 목록 반환.
     */
    @Transactional(readOnly = true)
    public Optional<StarterPackageResponseDTO> getByGuestSessionId(String guestSessionId) {
        if (guestSessionId == null || guestSessionId.isBlank()) {
            return Optional.empty();
        }
        return guestSessionRepository.findById(guestSessionId)
                .map(GuestSessionEntity::getStarterType)
                .flatMap(type ->
                        starterPackageRepository.findByStarterPackageNameAndIsUseTrue(type)
                                .map(pkg -> {
                                    var items = starterPackageItemRepository
                                            .findByStarterPackage_StarterPackageIdOrderByRankAsc(pkg.getStarterPackageId());
                                    return StarterPackageResponseDTO.fromEntity(guestSessionId, type, items);
                                })
                );
    }
}
