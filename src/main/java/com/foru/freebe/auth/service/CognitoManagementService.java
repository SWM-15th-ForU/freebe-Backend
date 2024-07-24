package com.foru.freebe.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.foru.freebe.auth.entity.KakaoUser;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;

@Service
public class CognitoManagementService {
	@Value("${AWS_ACCESS_KEY_ID}")
	private String awsAccessKeyId;
	@Value("${AWS_SECRET_ACCESS_KEY}")
	private String awsSecretAccessKey;
	@Value("${AWS_REGION}")
	private String awsRegion;
	@Value("${COGNITO_USER_POOL_ID}")
	private String cognitoUserPoolId;

	public void registerUserPool(KakaoUser kakaoUser) {
		AwsBasicCredentials credentials = AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey);

		CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
			.region(Region.of(awsRegion))
			.credentialsProvider(StaticCredentialsProvider.create(credentials))
			.build();

		AdminCreateUserRequest createUserRequest = AdminCreateUserRequest.builder()
			.messageAction("SUPPRESS")
			.userPoolId(cognitoUserPoolId)
			.username(kakaoUser.getEmail())
			.userAttributes(
				AttributeType.builder().name("email").value(kakaoUser.getEmail()).build(),
				AttributeType.builder().name("phone_number").value(kakaoUser.getPhoneNumber()).build(),
				AttributeType.builder().name("name").value(kakaoUser.getUserName()).build()
			)
			.build();

		AdminCreateUserResponse adminCreateUserResponse = cognitoClient.adminCreateUser(createUserRequest);

		System.out.println("adminCreateUserResponse : " + adminCreateUserResponse);
	}
}
