package com.foru.freebe.jwt.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.JwtErrorCode;
import com.foru.freebe.errors.exception.JwtTokenException;
import com.foru.freebe.jwt.model.JwtToken;
import com.foru.freebe.jwt.repository.JwtTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtVerifier {
    private final JwtProvider jwtProvider;
    private final JwtTokenRepository jwtTokenRepository;

    private void validateTokenExpiration(String token) {
        if (jwtProvider.getExpiration(token).isBefore(LocalDateTime.now())) {
            throw new JwtTokenException(JwtErrorCode.EXPIRED_TOKEN);
        }
    }

    private void validateRefreshTokenRevocation(JwtToken refreshToken) {
        if (refreshToken.getIsRevoked()) {
            throw new JwtTokenException(JwtErrorCode.REVOKED_TOKEN);
        }
    }

    public void validateRefreshToken(JwtToken refreshToken) {
        validateTokenExpiration(refreshToken.getRefreshToken());
        validateRefreshTokenRevocation(refreshToken);
    }

    public boolean isAccessTokenValid(String accessToken) {
        validateTokenExpiration(accessToken);

        Long memberId = jwtProvider.getMemberIdFromToken(accessToken);
        JwtToken refreshToken = jwtTokenRepository.findByMemberId(memberId)
            .orElseThrow(() -> new JwtTokenException(JwtErrorCode.TOKEN_NOT_FOUND));

        validateRefreshToken(refreshToken);
        return true;
    }
}
