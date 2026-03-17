package com.snowman.team2.domain.starterPackage.entity;

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
}
