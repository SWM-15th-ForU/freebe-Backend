package com.foru.freebe.jwt.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.foru.freebe.errors.errorcode.JwtErrorCode;
import com.foru.freebe.errors.exception.JwtTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${JWT_SECRET_KEY}")
    private String jwtSecretKey;
    private SecretKey secretKey;

    private static final long ACCESS_TOKEN_TIME = 1000 * 60 * 30;
    private static final long REFRESH_TOKEN_TIME = 1000 * 60 * 60 * 24 * 14;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    }

    public Long getMemberIdFromToken(String token) {
        return Long.valueOf(parseClaims(token).getPayload().get("memberId", String.class));
    }

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

    public LocalDateTime getExpiration(String token) {
        Jws<Claims> claims = parseClaims(token);
        Date expiration = claims.getPayload().getExpiration();
        return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public Jws<Claims> parseClaims(String token) {
        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new JwtTokenException(JwtErrorCode.EXPIRED_ACCESS_TOKEN);
        } catch (JwtException e) {
            throw new JwtTokenException(JwtErrorCode.INVALID_TOKEN);
        }
    }
}