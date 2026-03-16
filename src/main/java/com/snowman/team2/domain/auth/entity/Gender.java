package com.snowman.team2.domain.auth.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Gender {
    M("남자"),
    F("여자");

    private final String description;

    Gender(String description) {
        this.description = description;
    }

    /** API 요청/응답: "MALE", "FEMALE" 사용 */
    @JsonValue
    public String toApiValue() {
        return this == M ? "MALE" : "FEMALE";
    }

    @JsonCreator
    public static Gender fromApiValue(String value) {
        if (value == null) return null;
        return Arrays.stream(values())
                .filter(g -> g.toApiValue().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 gender: " + value));
    }
}