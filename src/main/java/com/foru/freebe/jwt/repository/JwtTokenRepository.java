package com.foru.freebe.jwt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.jwt.model.JwtToken;

public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    Optional<List<JwtToken>> findByMemberId(Long memberId);

    Optional<JwtToken> findByRefreshToken(String refreshToken);

    Void deleteByRefreshToken(String refreshToken);
}
