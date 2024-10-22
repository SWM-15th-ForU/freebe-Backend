package com.foru.freebe.jwt.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.foru.freebe.auth.model.CustomUserDetails;
import com.foru.freebe.auth.service.CustomUserDetailsService;
import com.foru.freebe.errors.errorcode.JwtErrorCode;
import com.foru.freebe.errors.exception.JwtTokenException;
import com.foru.freebe.jwt.model.JwtToken;
import com.foru.freebe.jwt.model.JwtTokenModel;
import com.foru.freebe.jwt.repository.JwtTokenRepository;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.Role;
import com.foru.freebe.member.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtProvider jwtProvider;
    private final JwtVerifier jwtVerifier;
    private final JwtTokenRepository jwtTokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public JwtTokenModel generateToken(Long id) {
        String accessToken = jwtProvider.generateAccessToken(id);
        String refreshToken = jwtProvider.generateRefreshToken(id);

        saveRefreshToken(id, refreshToken);

        return new JwtTokenModel(accessToken, refreshToken);
    }

    @Transactional
    public JwtTokenModel reissueToken(String refreshToken) {
        jwtVerifier.validateRefreshToken(refreshToken);

        JwtToken oldToken = findRefreshToken(refreshToken);
        Long memberId = jwtProvider.getMemberIdFromToken(refreshToken);

        jwtVerifier.validateRefreshTokenRevocation(oldToken);

        if (isTokenExpiringSoon(refreshToken)) {
            oldToken.revokeToken();
            return generateToken(memberId);
        }

        return reissueAccessToken(refreshToken, memberId);
    }

    @Transactional
    public void revokeTokenOnLogout(String token) {
        JwtToken refreshToken = findRefreshToken(token);

        refreshToken.revokeToken();
    }

    @Transactional
    public void revokeRefreshTokenByUserId(Long id) {
        List<JwtToken> tokenList = getTokenFromMemberId(id);

        for (JwtToken token : tokenList) {
            token.revokeToken();
        }
    }

    public Authentication getAuthentication(String token) {
        String memberId = String.valueOf(jwtProvider.getMemberIdFromToken(token));
        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(memberId);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public Role getMemberRole(String token) {
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new JwtTokenException(JwtErrorCode.INVALID_TOKEN));

        return member.getRole();
    }

    public HttpHeaders setTokenHeaders(JwtTokenModel token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", token.getAccessToken());
        headers.add("refreshToken", token.getRefreshToken());
        return headers;
    }

    private JwtToken findRefreshToken(String refreshToken) {
        return jwtTokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new JwtTokenException(JwtErrorCode.TOKEN_NOT_FOUND));
    }

    private List<JwtToken> getTokenFromMemberId(Long memberId) {
        return jwtTokenRepository.findByMemberId(memberId)
            .orElseThrow(() -> new JwtTokenException(JwtErrorCode.TOKEN_NOT_FOUND));
    }

    private boolean isTokenExpiringSoon(String refreshToken) {
        LocalDateTime expirationDate = jwtProvider.getExpiration(refreshToken);
        return expirationDate.plusDays(3).isBefore(LocalDateTime.now());
    }

    private void saveRefreshToken(Long id, String refreshToken) {
        JwtToken newToken = JwtToken.builder()
            .memberId(id)
            .refreshToken(refreshToken)
            .expiresAt(jwtProvider.getExpiration(refreshToken))
            .build();
        jwtTokenRepository.save(newToken);
    }

    private JwtTokenModel reissueAccessToken(String refreshToken, Long memberId) {
        String newAccessToken = jwtProvider.generateAccessToken(memberId);
        return new JwtTokenModel(newAccessToken, refreshToken);
    }
}