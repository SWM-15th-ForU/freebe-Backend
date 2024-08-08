package com.foru.freebe.jwt.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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
}