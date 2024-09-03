package com.foru.freebe.config;

import java.io.IOException;

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

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            String refreshToken = request.getHeader("refreshToken");
            if (refreshToken == null || refreshToken.isEmpty()) {
                throw new JwtTokenException(JwtErrorCode.INVALID_TOKEN);
            }
            Role role = jwtService.getMemberRole(refreshToken);
            jwtService.revokeToken(refreshToken);

            switch (role) {
                case CUSTOMER -> response.sendRedirect("/login/customer");
                case PHOTOGRAPHER, PHOTOGRAPHER_PENDING -> response.sendRedirect("/login/photographer");
                default -> response.sendRedirect("/login");
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
