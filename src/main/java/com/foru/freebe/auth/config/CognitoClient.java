package com.foru.freebe.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
public class CognitoClient {
	private final AwsProperties awsProperties;

	public CognitoClient(AwsProperties awsProperties) {
		this.awsProperties = awsProperties;
	}

	@Bean
	public CognitoIdentityProviderClient createCognitoIdentityProviderClient() {
		AwsBasicCredentials credentials = AwsBasicCredentials.create(awsProperties.getAccessKeyId(),
			awsProperties.getSecretAccessKey());

		return CognitoIdentityProviderClient.builder()
			.region(Region.of(awsProperties.getRegion()))
			.credentialsProvider(StaticCredentialsProvider.create(credentials))
			.build();
	}
}
