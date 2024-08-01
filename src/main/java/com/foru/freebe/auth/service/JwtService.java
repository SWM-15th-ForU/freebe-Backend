package com.foru.freebe.auth.service;

import org.springframework.stereotype.Service;

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

		return new JwtTokenModel(accessToken, refreshToken);
	}
}