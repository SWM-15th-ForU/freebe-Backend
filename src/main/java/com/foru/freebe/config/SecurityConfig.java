package com.foru.freebe.config;

import static org.springframework.security.config.Customizer.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.foru.freebe.auth.config.CustomAuthenticationSuccessHandler;
import com.foru.freebe.auth.config.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf((csrf) -> csrf.disable())
			.cors(withDefaults())
			.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			.authorizeHttpRequests((request) -> request
				.requestMatchers("/photographer").hasAnyRole("PHOTOGRAPHER")
				.requestMatchers("/admin").hasAnyRole("ADMIN")
				.anyRequest().permitAll())

			.oauth2Login((oauth2) -> oauth2
				.defaultSuccessUrl("/")
				.failureUrl("/fail")
				.successHandler(customAuthenticationSuccessHandler))

			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
