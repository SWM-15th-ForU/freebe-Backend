package com.foru.freebe.auth.config;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.foru.freebe.auth.service.JwtProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtProvider jwtProvider;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String[] excludePath = {"/login", "/"};
		String path = request.getRequestURI();
		return Arrays.stream(excludePath).anyMatch(path::startsWith);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		Cookie[] cookies = request.getCookies();
		String accessToken = null;
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("accessToken")) {
				accessToken = cookie.getValue();
			}
		}

		filterChain.doFilter(request, response);
	}
}