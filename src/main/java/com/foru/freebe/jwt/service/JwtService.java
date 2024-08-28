package com.foru.freebe.jwt.service;

import java.util.List;
import java.util.Optional;

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

    public JwtTokenModel reissueRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new JwtTokenException(JwtErrorCode.INVALID_TOKEN);
        }
        if (!jwtProvider.isTokenValidate(refreshToken)) {
            throw new JwtTokenException(JwtErrorCode.EXPIRED_TOKEN);
        }

        JwtToken jwtToken = jwtTokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new JwtTokenException(JwtErrorCode.INVALID_TOKEN));

        Long memberId = jwtToken.getMemberId();
        jwtTokenRepository.delete(jwtToken);

        return generateToken(memberId);
    }

    private void saveRefreshToken(Long id, String refreshToken) {
        Optional<List<JwtToken>> jwtToken = jwtTokenRepository.findByMemberId(id);
        jwtToken.ifPresent(jwtTokenRepository::deleteAll);
        JwtToken newToken = JwtToken.createJwtToken(id, refreshToken);
        jwtTokenRepository.save(newToken);
    }

    public HttpHeaders setTokenHeaders(JwtTokenModel token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", token.getAccessToken());
        headers.add("refreshToken", token.getRefreshToken());
        return headers;
    }
}