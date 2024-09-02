package com.foru.freebe.jwt.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @NotNull
    private Long memberId;

    @NotNull
    private String refreshToken;

    @CreationTimestamp
    private LocalDateTime issuedAt;

    @NotNull
    private LocalDateTime expiresAt;

    @NotNull
    private Boolean isRevoked;

    @Builder
    private JwtToken(Long memberId, String refreshToken, LocalDateTime expiresAt) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
        this.issuedAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.isRevoked = false;
    }
}