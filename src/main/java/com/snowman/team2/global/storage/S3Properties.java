package com.snowman.team2.global.storage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.s3")
public class S3Properties {

    @NotBlank
    private String bucket;

    /** S3 리전 (SDK·presigned URL) */
    @NotBlank
    private String region;

    /** 프론트에 내려줄 presigned GET URL 유효 시간(분) */
    @Min(1)
    private int presignedGetTtlMinutes;
}
