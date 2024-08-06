package com.foru.freebe.auth.service;

import org.springframework.stereotype.Service;

import com.foru.freebe.auth.model.JwtTokenEntity;
import com.foru.freebe.auth.model.JwtTokenModel;
import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.auth.repository.JwtTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtTokenRepository jwtTokenRepository;
    private final JwtProvider jwtProvider;

    public JwtTokenModel generateToken(KakaoUser kakaoUser) {
        String accessToken = jwtProvider.generateAccessToken(kakaoUser.getName());
        String refreshToken = jwtProvider.generateRefreshToken(kakaoUser.getName());

        saveRefreshToken(kakaoUser.getName(), refreshToken);

        return new JwtTokenModel(accessToken, refreshToken);
    }

    private void saveRefreshToken(Long kakaoId, String refreshToken) {
        JwtTokenEntity jwtToken = JwtTokenEntity.createJwtToken(kakaoId, refreshToken);
        jwtTokenRepository.save(jwtToken);
    }
}