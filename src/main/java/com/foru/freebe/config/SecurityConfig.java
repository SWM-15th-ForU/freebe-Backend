package com.foru.freebe.config;

import static org.springframework.security.config.Customizer.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf((csrf) -> csrf
				.disable())
			.cors(withDefaults())

			.authorizeHttpRequests((request) -> request
				.requestMatchers("/user/**").authenticated()
				.requestMatchers("/user").hasAnyRole("USER")
				.requestMatchers("/admin").hasAnyRole("ADMIN")
				.anyRequest().permitAll())

			.oauth2Login((oauth2) -> oauth2
				.defaultSuccessUrl("/user")
				.failureUrl("/")
				.successHandler(customAuthenticationSuccessHandler));
		return http.build();
	}
}
