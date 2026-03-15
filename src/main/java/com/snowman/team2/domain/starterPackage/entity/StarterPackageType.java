package com.snowman.team2.domain.starterPackage.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StarterPackageType {
    SELF_LIVING("자취"),
    NEWLYWED("신혼"),
    PET("반려동물"),
    MOVE_REMODEL("이사/리모델링"),
    COMPACT("소형가전"),
    OTHER("기타");

    private final String description;

}
