package com.foru.freebe.jwt.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.JwtErrorCode;
import com.foru.freebe.errors.exception.JwtTokenException;
import com.foru.freebe.jwt.model.JwtTokenEntity;
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

    private void saveRefreshToken(Long id, String refreshToken) {
        Optional<List<JwtTokenEntity>> jwtToken = jwtTokenRepository.findByMemberId(id);
        jwtToken.ifPresent(jwtTokenRepository::deleteAll);
        JwtTokenEntity newToken = JwtTokenEntity.createJwtToken(id, refreshToken);
        jwtTokenRepository.save(newToken);
    }

    public JwtTokenModel reissueRefreshToken(String refreshToken) {
        if (refreshToken == null || !jwtProvider.isTokenValidate(refreshToken)) {
            throw new JwtTokenException(JwtErrorCode.INVALID_TOKEN);
        }

        JwtTokenEntity jwtToken = jwtTokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new JwtTokenException(JwtErrorCode.INVALID_TOKEN));

        Long memberId = jwtToken.getMemberId();
        jwtTokenRepository.delete(jwtToken);

        return generateToken(memberId);
    }
}