package com.snowman.team2.domain.starterPackage.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.snowman.team2.global.exception.ErrorCode;
import com.snowman.team2.global.exception.exceptionType.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StarterPackageType {
    SINGLE("싱글라이프"),
    NEWLYWEDS("신혼 부부"),
    WITH_BABY("아기가 있는 집"),
    WITH_STUDENT("취학 자녀가 있는 집"),
    WITH_PARENTS("부모님과 함께 사는 집"),
    OTHER("기타");

    private final String description;

    /**
     * JSON으로 들어온 값이 enum name(SINGLE 등) 또는 description(싱글라이프 등) 중 무엇이든 받을 수 있게 처리
     */
    @JsonCreator
    public static StarterPackageType from(String value) {
        if (value == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "starter_package_type을 입력해주세요.");
        }

        String normalized = value.trim();
        for (StarterPackageType type : StarterPackageType.values()) {
            if (type.name().equalsIgnoreCase(normalized)) {
                return type;
            }
            if (type.description.equals(normalized)) {
                return type;
            }
        }

        throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "알 수 없는 starter_package_type: " + value);
    }
}
