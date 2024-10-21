package com.foru.freebe.jwt.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.foru.freebe.errors.exception.JwtTokenException;
import com.foru.freebe.errors.exception.RestApiException;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtTokenException e) {
            response.setStatus(e.getHttpStatus());
            response.setContentType("application/json");
            response.getWriter()
                .write(
                    "{\"message\":\"" + e.getMessage() + "\"}");
        } catch (JwtException e) {
            response.setStatus(400);
            response.setContentType("application/json");
            response.getWriter()
                .write(
                    "{\"message\":\"" + e.getMessage() + "\"}");
        } catch (RestApiException e) {
            response.setStatus(e.getErrorCode().getHttpStatus());
            response.setContentType("application/json");
            response.getWriter()
                .write(
                    "{\"message\":\"" + e.getMessage() + "\"}"
                );
        }
    }
}