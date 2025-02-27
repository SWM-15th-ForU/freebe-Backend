package com.foru.freebe.config;

import static org.springframework.security.config.Customizer.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import com.foru.freebe.jwt.filter.JwtAuthenticationFilter;
import com.foru.freebe.jwt.filter.JwtExceptionFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomLogoutHandler customLogoutHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(withDefaults())
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtExceptionFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtExceptionFilter, LogoutFilter.class)

            .authorizeHttpRequests((request) -> request
                .requestMatchers("/photographer/join").hasAnyRole("PHOTOGRAPHER_PENDING")
                .requestMatchers("/photographer/**").hasAnyRole("PHOTOGRAPHER")
                .requestMatchers("/customer/product/**").permitAll()
                .requestMatchers("/customer/profile/**").permitAll()
                .requestMatchers("/customer/notice/**").permitAll()
                .requestMatchers("/customer/schedule/**").permitAll()
                .requestMatchers("/customer/**").hasAnyRole("CUSTOMER", "PHOTOGRAPHER")
                .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                .anyRequest().permitAll())

            .logout((logout) -> logout
                .logoutUrl("/logout")
                .addLogoutHandler(customLogoutHandler))

            .exceptionHandling((handler) -> handler
                .accessDeniedHandler(customAccessDeniedHandler));

        return http.build();
    }
}
