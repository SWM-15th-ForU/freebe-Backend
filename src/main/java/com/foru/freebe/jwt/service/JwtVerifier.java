package com.foru.freebe.jwt.service;

import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.JwtErrorCode;
import com.foru.freebe.errors.exception.JwtTokenException;
import com.foru.freebe.jwt.model.JwtToken;
import com.foru.freebe.jwt.repository.JwtTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtVerifier {
    private final JwtProvider jwtProvider;
    private final JwtTokenRepository jwtTokenRepository;

    public boolean isAccessTokenValid(String accessToken) {
        Jws<Claims> claimsJws = jwtProvider.parseClaims(accessToken);
        return true;
    }

    public void validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new JwtTokenException(JwtErrorCode.MISSING_TOKEN);
        }
    }

    public void validateRefreshTokenRevocation(JwtToken refreshToken) {
        if (refreshToken.getIsRevoked()) {
            throw new JwtTokenException(JwtErrorCode.REVOKED_TOKEN);
        }
    }
}
