package com.foru.freebe.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.errorcode.JwtErrorCode;
import com.foru.freebe.errors.exception.JwtTokenException;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.jwt.service.JwtService;
import com.foru.freebe.member.entity.Role;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {
    private final JwtService jwtService;

    @Value("${FREEBE_BASE_URL}")
    private String freebeBaseUrl;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            String refreshToken = request.getHeader("refreshToken");
            if (refreshToken == null || refreshToken.isEmpty()) {
                throw new JwtTokenException(JwtErrorCode.INVALID_TOKEN);
            }
            Role role = jwtService.getMemberRole(refreshToken);
            jwtService.revokeTokenOnLogout(refreshToken);

            switch (role) {
                case CUSTOMER -> response.sendRedirect(freebeBaseUrl + "/login/customer");
                case PHOTOGRAPHER, PHOTOGRAPHER_PENDING -> response.sendRedirect(freebeBaseUrl + "/login/photographer");
                default -> response.sendRedirect(freebeBaseUrl + "/login");
            }
        } catch (JwtTokenException e) {
            throw new JwtTokenException(e.getErrorCode());
        } catch (JwtException e) {
            throw new JwtException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RestApiException(CommonErrorCode.IO_EXCEPTION);
        }
    }
}
