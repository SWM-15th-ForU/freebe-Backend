package com.foru.freebe.auth.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {
	SecretKey secretKey = Jwts.SIG.HS256.key().build();
	public static final long ACCESS_TOKEN_TIME = 1000 * 60 * 30;
	public static final long REFRESH_TOKEN_TIME = 1000 * 60 * 60 * 24 * 14;
	private final CustomUserDetailsService userDetailsService;

	public String generateAccessToken(Long kakaoId) {
		Claims claims = Jwts.claims()
			.issuer(String.valueOf(kakaoId))
			.add("kakaoId", String.valueOf(kakaoId))
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
			.add("kakaoId", String.valueOf(kakaoId))
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIME))
			.build();

		return Jwts.builder()
			.claims(claims)
			.signWith(secretKey)
			.compact();
	}

	private Jws<Claims> parseClaims(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token);
	}

	public boolean isTokenValidate(String token) {
		try {
			Jws<Claims> claims = parseClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public Authentication getAuthentication(String token) {
		Jws<Claims> claims = parseClaims(token);
		CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(
			claims.getPayload().get("kakaoId", String.class));

		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}
}