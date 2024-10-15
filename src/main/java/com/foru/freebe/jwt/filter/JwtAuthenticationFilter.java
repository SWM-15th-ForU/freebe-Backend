package com.foru.freebe.jwt.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.foru.freebe.errors.errorcode.JwtErrorCode;
import com.foru.freebe.errors.exception.JwtTokenException;
import com.foru.freebe.jwt.service.JwtService;
import com.foru.freebe.jwt.service.JwtVerifier;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtVerifier jwtVerifier;
    private final JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        List<RequestMatcher> matchers = new ArrayList<>();
        matchers.add(new AntPathRequestMatcher("/customer/product/**"));
        matchers.add(new AntPathRequestMatcher("/customer/profile/**"));
        matchers.add(new AntPathRequestMatcher("/customer/notice/**"));
        matchers.add(new AntPathRequestMatcher("/login/**"));
        matchers.add(new AntPathRequestMatcher("/reissue"));

        return matchers.stream()
            .anyMatch((matcher -> matcher.matches(request)));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        try {
            String accessToken = request.getHeader("Authorization");

            if (accessToken == null || !accessToken.startsWith("Bearer ")) {
                throw new JwtTokenException(JwtErrorCode.INVALID_TOKEN);
            }

            accessToken = accessToken.substring(7);
            if (jwtVerifier.isAccessTokenValid(accessToken)) {
                Authentication auth = jwtService.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (ExpiredJwtException e) {
            throw new JwtTokenException(JwtErrorCode.EXPIRED_TOKEN);
        } catch (JwtException e) {
            throw new JwtException(e.getMessage(), e);
        }
        filterChain.doFilter(request, response);
    }
}