package com.foru.freebe.auth.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {
	SecretKey secretKey = Jwts.SIG.HS256.key().build();
	public static final long ACCESS_TOKEN_TIME = 1000 * 60 * 30;
	public static final long REFRESH_TOKEN_TIME = 1000 * 60 * 60 * 24 * 14;

	public String generateAccessToken(Long kakaoId) {
		Claims claims = Jwts.claims()
			.issuer(String.valueOf(kakaoId))
			.add("kakaoId", kakaoId)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_TIME))
			.build();

		return Jwts.builder()
			.claims(claims)
			.signWith(secretKey)
			.compact();
	}

	public String generateRefreshToken(Long kakaoId) {
		Claims claims = Jwts.claims()
			.issuer(String.valueOf(kakaoId))
			.add("kakaoId", kakaoId)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIME))
			.build();

		return Jwts.builder()
			.claims(claims)
			.signWith(secretKey)
			.compact();
	}
}