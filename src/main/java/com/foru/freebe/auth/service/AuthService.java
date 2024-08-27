package com.foru.freebe.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.foru.freebe.auth.dto.TokenResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final WebClient webClient;

    @Value("${KAKAO_CLIENT_ID}")
    private String clientId;

    @Value("${KAKAO_REDIRECT_URI}")
    private String redirectUri;

    @Value("${KAKAO_CLIENT_SECRET}")
    private String clientSecret;

    public String exchangeToken(String code) {
        Mono<TokenResponse> tokenResponseMono = webClient.post()
            .uri("/oauth/token")
            .body(BodyInserters.fromFormData(getRequestBody(code)))
            .retrieve()
            .bodyToMono(TokenResponse.class);

        return tokenResponseMono.map(TokenResponse::getAccessToken).block();
    }

    private MultiValueMap<String, String> getRequestBody(String code) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", clientId);
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("code", code);
        requestBody.add("client_secret", clientSecret);
        return requestBody;
    }
}
