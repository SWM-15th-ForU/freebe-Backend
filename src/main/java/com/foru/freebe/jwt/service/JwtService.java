package com.foru.freebe.jwt.service;

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

    public Authentication getAuthentication(String token) {
        String memberId = String.valueOf(jwtProvider.getMemberIdFromToken(token));
        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(memberId);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Transactional
    public JwtTokenModel generateToken(Long id) {
        String accessToken = jwtProvider.generateAccessToken(id);
        String refreshToken = jwtProvider.generateRefreshToken(id);

        saveRefreshToken(id, refreshToken);

        return new JwtTokenModel(accessToken, refreshToken);
    }

    @Transactional
    public JwtTokenModel reissueToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new JwtTokenException(JwtErrorCode.INVALID_TOKEN);
        }

        Long memberId = jwtProvider.getMemberIdFromToken(refreshToken);
        JwtToken oldToken = getTokenFromMemberId(memberId);
        jwtVerifier.validateRefreshToken(oldToken);

        jwtTokenRepository.delete(oldToken);

        return generateToken(memberId);
    }

    public Role getMemberRole(String token) {
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new JwtTokenException(JwtErrorCode.INVALID_TOKEN));

        return member.getRole();
    }

    @Transactional
    public void revokeToken(String token) {
        JwtToken refreshToken = jwtTokenRepository.findByRefreshToken(token)
            .orElseThrow(() -> new JwtTokenException(JwtErrorCode.TOKEN_NOT_FOUND));

        refreshToken.revokeToken();
    }

    public HttpHeaders setTokenHeaders(JwtTokenModel token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", token.getAccessToken());
        headers.add("refreshToken", token.getRefreshToken());
        return headers;
    }

    public void deleteRefreshTokenByUserId(Long id) {
        JwtToken token = getTokenFromMemberId(id);
        jwtTokenRepository.delete(token);
    }

    private JwtToken getTokenFromMemberId(Long memberId) {
        return jwtTokenRepository.findByMemberId(memberId)
            .orElseThrow(() -> new JwtTokenException(JwtErrorCode.TOKEN_NOT_FOUND));
    }

    private void saveRefreshToken(Long id, String refreshToken) {
        jwtTokenRepository.findByMemberId(id).ifPresent(jwtTokenRepository::delete);

        JwtToken newToken = JwtToken.builder()
            .memberId(id)
            .refreshToken(refreshToken)
            .expiresAt(jwtProvider.getExpiration(refreshToken))
            .build();
        jwtTokenRepository.save(newToken);
    }
}