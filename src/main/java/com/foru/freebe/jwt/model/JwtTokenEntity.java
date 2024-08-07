package com.foru.freebe.jwt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class JwtTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @NotNull
    private Long kakaoId;

    @NotNull
    private String refreshToken;

    private JwtTokenEntity(Long kakaoId, String refreshToken) {
        this.kakaoId = kakaoId;
        this.refreshToken = refreshToken;
    }

    public static JwtTokenEntity createJwtToken(Long kakaoId, String refreshToken) {
        return new JwtTokenEntity(kakaoId, refreshToken);
    }
}