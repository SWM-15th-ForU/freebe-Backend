package com.foru.freebe.jwt.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.foru.freebe.auth.model.CustomUserDetails;
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
        CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
        JwtTokenModel token = jwtService.generateToken(userDetails.getMemberId());

        String redirectUrl = baseUrl + "/login/redirect?accessToken=" + token.getAccessToken() + "&refreshToken="
            + token.getRefreshToken();
        response.sendRedirect(redirectUrl);
    }
}