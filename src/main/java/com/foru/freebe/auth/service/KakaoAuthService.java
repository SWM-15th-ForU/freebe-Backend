package com.foru.freebe.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.foru.freebe.auth.model.KakaoToken;
import com.foru.freebe.auth.model.KakaoUser;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {
    private final WebClient kakaoLoginWebClient;

    @Value("${KAKAO_CLIENT_ID}")
    private String clientId;

    @Value("${KAKAO_REDIRECT_URI}")
    private String redirectUri;

    @Value("${KAKAO_CLIENT_SECRET}")
    private String clientSecret;

    public String getToken(String code) {
        Mono<KakaoToken> kakaoToken = kakaoLoginWebClient.post()
            .uri("/oauth/token")
            .body(BodyInserters.fromFormData(buildRequestBody(code)))
            .retrieve()
            .bodyToMono(KakaoToken.class);

        return kakaoToken.map(KakaoToken::getAccessToken).block();
    }

    public KakaoUser getUserInfo(String accessToken) {
        WebClient client = buildMutateWebClient(accessToken);

        return client.get()
            .uri(uriBuilder -> uriBuilder
                .path("v2/user/me")
                .queryParam("property_keys",
                    "[\"kakao_account.email\", \"kakao_account.name\", \"kakao_account.birthday\", \"kakao_account.birthyear\", \"kakao_account.phone_number\", \"kakao_account.gender\"]")
                .build()
            )
            .retrieve()
            .bodyToMono(KakaoUser.class)
            .block();
    }

    private MultiValueMap<String, String> buildRequestBody(String code) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", clientId);
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("code", code);
        requestBody.add("client_secret", clientSecret);
        return requestBody;
    }

    private WebClient buildMutateWebClient(String accessToken) {
        return kakaoLoginWebClient.mutate()
            .baseUrl("https://kapi.kakao.com")
            .defaultHeader("Authorization", "Bearer " + accessToken)
            .build();
    }
}
