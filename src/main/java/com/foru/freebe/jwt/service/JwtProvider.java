package com.foru.freebe.jwt.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.foru.freebe.auth.model.CustomUserDetails;
import com.foru.freebe.auth.service.CustomUserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final CustomUserDetailsService customUserDetailsService;
    private static final long ACCESS_TOKEN_TIME = 1000 * 60 * 30;
    // private static final long ACCESS_TOKEN_TIME = 1000 * 5; //5초
    private static final long REFRESH_TOKEN_TIME = 1000 * 60 * 60 * 24 * 14;
    private static final SecretKey secretKey = Jwts.SIG.HS256.key().build();

    public String generateAccessToken(Long id) {
        Claims claims = Jwts.claims()
            .issuer(String.valueOf(id))
            .add("memberId", String.valueOf(id))
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_TIME))
            .build();

        return Jwts.builder()
            .claims(claims)
            .signWith(secretKey)
            .compact();
    }

    public String generateRefreshToken(Long id) {
        Claims claims = Jwts.claims()
            .issuer(String.valueOf(id))
            .add("memberId", String.valueOf(id))
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
        Jws<Claims> claims = parseClaims(token);
        return true;
    }

    public Authentication getAuthentication(String token) {
        Jws<Claims> claims = parseClaims(token);
        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(
            claims.getPayload().get("memberId", String.class));

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}