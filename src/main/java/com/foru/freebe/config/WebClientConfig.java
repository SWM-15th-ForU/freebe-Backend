package com.foru.freebe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient
            .builder()
            .baseUrl("https://kauth.kakao.com")
            .defaultHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
            .build();
    }
}
