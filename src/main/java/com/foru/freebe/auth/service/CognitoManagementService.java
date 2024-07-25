package com.foru.freebe.auth.service;

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
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;

@Service
public class CognitoManagementService {
	private final CognitoProperties cognitoProperties;
	private final CognitoIdentityProviderClient cognitoClient;

	public CognitoManagementService(CognitoProperties cognitoProperties, CognitoIdentityProviderClient cognitoClient) {
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



	}
}
