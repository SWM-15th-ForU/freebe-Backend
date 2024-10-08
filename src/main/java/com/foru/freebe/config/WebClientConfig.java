package com.foru.freebe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebClientConfig {
    @Value("${kakao.alimtalk.api-server-host}")
    private String apiServerHost;

    @Bean
    public WebClient kakaoLoginWebClient() {
        return WebClient
            .builder()
            .baseUrl("https://kauth.kakao.com")
            .defaultHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
            .build();
    }

    @Bean
    public WebClient kakaoMessageWebClient() {
        return WebClient
            .builder()
            .baseUrl(apiServerHost)
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
}
