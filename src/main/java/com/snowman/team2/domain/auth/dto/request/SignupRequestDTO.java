package com.snowman.team2.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.snowman.team2.domain.auth.entity.Gender;
import com.snowman.team2.domain.auth.entity.UserEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignupRequestDTO {

    @NotBlank(message = "guest_sessionId는 필수입니다")
    @JsonProperty("guest_sessionId")
    private String guestSessionId;

    @NotBlank(message = "password는 필수입니다")
    private String password;

    @NotBlank(message = "name은 필수입니다")
    private String name;

    @NotNull(message = "gender는 필수입니다")
    private Gender gender;

    @NotBlank(message = "phone은 필수입니다")
    private String phone;

    @NotBlank(message = "email은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "birthDate는 필수입니다")
    private String birthDate; // YYMMDD (예: "990101")

    @NotNull(message = "terms_accepted는 필수입니다")
    @JsonProperty("terms_accepted")
    private Boolean termsAccepted;

    @NotNull(message = "privacy_accepted는 필수입니다")
    @JsonProperty("privacy_accepted")
    private Boolean privacyAccepted;

    public UserEntity toEntity(String encodedPassword) {
        return UserEntity.builder()
                .userName(name)
                .password(encodedPassword)
                .phone(phone)
                .email(email)
                .gender(gender)
                .birthDate(birthDate)
                .termsAccepted(termsAccepted != null && termsAccepted)
                .privacyAccepted(privacyAccepted != null && privacyAccepted)
                .createDate(LocalDateTime.now())
                .build();
    }
}
