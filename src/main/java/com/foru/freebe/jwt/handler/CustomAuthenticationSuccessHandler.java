package com.foru.freebe.jwt.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.jwt.model.JwtTokenModel;
import com.foru.freebe.jwt.service.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    @Value("${FREEBE_BASE_URL}")
    private String baseUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        KakaoUser kakaoUser = new KakaoUser(oAuth2User);

        JwtTokenModel token = jwtService.generateToken(kakaoUser);

        String redirectUrl = baseUrl + "/login/redirect?accessToken=" + token.getAccessToken() + "&refreshToken="
            + token.getRefreshToken();
        response.sendRedirect(redirectUrl);
    }
}