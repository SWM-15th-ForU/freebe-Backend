package com.foru.freebe.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.foru.freebe.auth.config.CognitoProperties;
import com.foru.freebe.auth.entity.KakaoUser;

import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

@Service
public class CognitoUtil {
	private final CognitoProperties cognitoProperties;
	private final CognitoIdentityProviderClient cognitoClient;

	public CognitoUtil(CognitoProperties cognitoProperties, CognitoIdentityProviderClient cognitoClient) {
		this.cognitoProperties = cognitoProperties;
		this.cognitoClient = cognitoClient;
	}

	public void registerUserPool(KakaoUser kakaoUser) {
		AdminCreateUserRequest adminCreateUserRequest = registerUser(kakaoUser);
		AdminCreateUserResponse adminCreateUserResponse = cognitoClient.adminCreateUser(adminCreateUserRequest);

		AdminSetUserPasswordRequest adminSetUserPasswordRequest = setPermanentPassword(kakaoUser);
		AdminSetUserPasswordResponse adminSetUserPasswordResponse = cognitoClient.adminSetUserPassword(
			adminSetUserPasswordRequest);
	}

	private AdminCreateUserRequest registerUser(KakaoUser kakaoUser) {
		AdminCreateUserRequest createUserRequest = AdminCreateUserRequest.builder()
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
		return createUserRequest;
	}

	private AdminSetUserPasswordRequest setPermanentPassword(KakaoUser kakaoUser) {
		AdminSetUserPasswordRequest adminSetUserPasswordRequest = AdminSetUserPasswordRequest.builder()
			.userPoolId(cognitoProperties.getUserPoolId())
			.username(kakaoUser.getEmail())
			.password("PermenantPassword1!")
			.permanent(true)
			.build();
		return adminSetUserPasswordRequest;
	}

	public AuthenticationResultType generateToken(KakaoUser kakaoUser) {
		AdminInitiateAuthRequest adminInitiateAuthRequest = getAuthenticatedUser(kakaoUser);
		AdminInitiateAuthResponse adminInitiateAuthResponse = cognitoClient.adminInitiateAuth(adminInitiateAuthRequest);

		return adminInitiateAuthResponse.authenticationResult();
	}

	private AdminInitiateAuthRequest getAuthenticatedUser(KakaoUser kakaoUser) {
		Map<String, String> authParams = new HashMap<>();
		authParams.put("USERNAME", kakaoUser.getEmail());
		authParams.put("PASSWORD", "PermenantPassword1!");
		authParams.put("SECRET_HASH", calculateSecretHash(cognitoProperties.getClientId(),
			cognitoProperties.getClientSecret(),
			kakaoUser.getEmail()));

		AdminInitiateAuthRequest adminInitiateAuthRequest = AdminInitiateAuthRequest.builder()
			.userPoolId(cognitoProperties.getUserPoolId())
			.authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
			.authParameters(authParams)
			.clientId(cognitoProperties.getClientId())
			.build();
		return adminInitiateAuthRequest;
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
