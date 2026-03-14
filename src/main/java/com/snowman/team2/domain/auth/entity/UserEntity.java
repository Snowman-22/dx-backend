package com.snowman.team2.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"User\"")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long user_id;

    @Column(name = "user_name", nullable = false)
    private String user_name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "birth_date")
    private LocalDateTime birth_date;

    @Column(name = "terms_accepted", nullable = false)
    @Builder.Default
    private Boolean terms_accepted = false;

    @Column(name = "privacy_accepted", nullable = false)
    @Builder.Default
    private Boolean privacy_accepted = false;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime create_date;

}
