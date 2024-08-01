package com.foru.freebe.auth.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.foru.freebe.auth.config.CognitoProperties;
import com.foru.freebe.auth.model.KakaoUser;

import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;

@Service
public class CognitoRegistrationService {
	private final CognitoProperties cognitoProperties;
	private final CognitoIdentityProviderClient cognitoClient;

	public CognitoRegistrationService(CognitoProperties cognitoProperties,
		CognitoIdentityProviderClient cognitoClient) {
		this.cognitoProperties = cognitoProperties;
		this.cognitoClient = cognitoClient;
	}

	public void registerIfUserNotInCognito(KakaoUser kakaoUser) {
		if (isUserNotInCognito(kakaoUser)) {
			registerUserWithTemporaryPassword(kakaoUser);
			setPermanentPassword(kakaoUser);
		}
	}

	public AuthenticationResultType generateToken(KakaoUser kakaoUser) {
		AdminInitiateAuthRequest adminInitiateAuthRequest = createAuthRequest(kakaoUser);
		AdminInitiateAuthResponse adminInitiateAuthResponse = cognitoClient.adminInitiateAuth(adminInitiateAuthRequest);

		return adminInitiateAuthResponse.authenticationResult();
	}

	private boolean isUserNotInCognito(KakaoUser kakaoUser) {
		try {
			AdminGetUserRequest adminGetUserRequest = AdminGetUserRequest.builder()
				.userPoolId(cognitoProperties.getUserPoolId())
				.username(kakaoUser.getEmail())
				.build();
			cognitoClient.adminGetUser(adminGetUserRequest);
			return false;
		} catch (UserNotFoundException e) {
			return true;
		}
	}

	private void registerUserWithTemporaryPassword(KakaoUser kakaoUser) {
		AdminCreateUserRequest adminCreateUserRequest = AdminCreateUserRequest.builder()
			.messageAction("SUPPRESS")
			.userPoolId(cognitoProperties.getUserPoolId())
			.username(kakaoUser.getEmail())
			.temporaryPassword("TemporaryPassword1!")
			.userAttributes(
				AttributeType.builder().name("email").value(kakaoUser.getEmail()).build(),
				AttributeType.builder().name("phone_number").value(kakaoUser.getPhoneNumberFormatE164()).build(),
				AttributeType.builder().name("name").value(kakaoUser.getUserName()).build()
			)
			.build();

		cognitoClient.adminCreateUser(adminCreateUserRequest);
	}

	private void setPermanentPassword(KakaoUser kakaoUser) {
		AdminSetUserPasswordRequest adminSetUserPasswordRequest = AdminSetUserPasswordRequest.builder()
			.userPoolId(cognitoProperties.getUserPoolId())
			.username(kakaoUser.getEmail())
			.password("PermanentPassword1!")
			.permanent(true)
			.build();

		cognitoClient.adminSetUserPassword(adminSetUserPasswordRequest);
	}

	private AdminInitiateAuthRequest createAuthRequest(KakaoUser kakaoUser) {
		Map<String, String> authParams = createAuthParameters(kakaoUser);

		return AdminInitiateAuthRequest.builder()
			.userPoolId(cognitoProperties.getUserPoolId())
			.authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
			.authParameters(authParams)
			.clientId(cognitoProperties.getClientId())
			.build();
	}

	private Map<String, String> createAuthParameters(KakaoUser kakaoUser) {
		Map<String, String> authParams = new HashMap<>();
		authParams.put("USERNAME", kakaoUser.getEmail());
		authParams.put("PASSWORD", "PermanentPassword1!");
		authParams.put("SECRET_HASH", calculateSecretHash(cognitoProperties.getClientId(),
			cognitoProperties.getClientSecret(),
			kakaoUser.getEmail()));

		return authParams;
	}

	private static String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
		final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

		SecretKeySpec signingKey = new SecretKeySpec(
			userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
			HMAC_SHA256_ALGORITHM);
		try {
			Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
			mac.init(signingKey);
			mac.update(userName.getBytes(StandardCharsets.UTF_8));
			byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(rawHmac);
		} catch (Exception e) {
			throw new RuntimeException("Error while calculating ");
		}
	}
}
