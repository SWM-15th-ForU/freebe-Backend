package com.foru.freebe.jwt.service;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.JwtErrorCode;
import com.foru.freebe.errors.exception.JwtTokenException;
import com.foru.freebe.jwt.model.JwtToken;
import com.foru.freebe.jwt.model.JwtTokenModel;
import com.foru.freebe.jwt.repository.JwtTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtTokenRepository jwtTokenRepository;
    private final JwtProvider jwtProvider;

    public JwtTokenModel generateToken(Long id) {
        String accessToken = jwtProvider.generateAccessToken(id);
        String refreshToken = jwtProvider.generateRefreshToken(id);

        saveRefreshToken(id, refreshToken);

        return new JwtTokenModel(accessToken, refreshToken);
    }

    public JwtTokenModel reissueToken(String refreshToken) {
        validateRefreshToken(refreshToken);

        JwtToken jwtToken = jwtTokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new JwtTokenException(JwtErrorCode.INVALID_TOKEN));

        Long memberId = jwtToken.getMemberId();
        jwtTokenRepository.delete(jwtToken);

        return generateToken(memberId);
    }

    public HttpHeaders setTokenHeaders(JwtTokenModel token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", token.getAccessToken());
        headers.add("refreshToken", token.getRefreshToken());
        return headers;
    }

    private void saveRefreshToken(Long id, String refreshToken) {
        jwtTokenRepository.findByMemberId(id).ifPresent(jwtTokenRepository::deleteAll);

        JwtToken newToken = JwtToken.builder()
            .memberId(id)
            .refreshToken(refreshToken)
            .expiresAt(jwtProvider.getExpiration(refreshToken))
            .build();
        jwtTokenRepository.save(newToken);
    }

    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new JwtTokenException(JwtErrorCode.INVALID_TOKEN);
        }
        if (!jwtProvider.isTokenValidate(refreshToken)) {
            throw new JwtTokenException(JwtErrorCode.EXPIRED_TOKEN);
        }
    }
}