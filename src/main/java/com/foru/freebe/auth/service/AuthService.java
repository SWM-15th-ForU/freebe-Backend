package com.foru.freebe.auth.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
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
    private final CustomOAuth2UserService customOAuth2UserService;

    @Value("${KAKAO_CLIENT_ID}")
    private String clientId;

    @Value("${KAKAO_REDIRECT_URI}")
    private String redirectUri;

    @Value("${KAKAO_CLIENT_SECRET}")
    private String clientSecret;

    public TokenResponse exchangeToken(String code) {
        Mono<TokenResponse> tokenResponseMono = webClient.post()
            .uri("/oauth/token")
            .body(BodyInserters.fromFormData(buildRequestBody(code)))
            .retrieve()
            .bodyToMono(TokenResponse.class);

        return tokenResponseMono.block();
    }

    public void getUserInfo(TokenResponse tokenResponse, String roleType) {
        ClientRegistration clientRegistration = buildClientRegistration();
        OAuth2AccessToken accessToken = buildOAuth2AccessToken(tokenResponse);

        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("roleType", roleType);

        OAuth2UserRequest oAuth2UserRequest = new OAuth2UserRequest(clientRegistration, accessToken,
            additionalParameters);
        customOAuth2UserService.loadUser(oAuth2UserRequest);
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

    private ClientRegistration buildClientRegistration() {
        return ClientRegistration.withRegistrationId("kakao")
            .clientId(clientId)
            .clientSecret(clientSecret)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope("account_email", "name", "birthday", "birthyear", "phone_number")
            .authorizationUri("https://kauth.kakao.com/oauth/authorize")
            .tokenUri("https://kauth.kakao.com/oauth/token")
            .clientName("Kakao")
            .build();
    }

    private OAuth2AccessToken buildOAuth2AccessToken(TokenResponse tokenResponse) {
        Set<String> scopes = new HashSet<>();
        scopes.add("birthday");
        scopes.add("account_email");
        scopes.add("birthyear");
        scopes.add("name");
        scopes.add("phone_number");

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(tokenResponse.getExpiresIn());

        return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
            tokenResponse.getAccessToken(), issuedAt, expiresAt, scopes);
    }

}
