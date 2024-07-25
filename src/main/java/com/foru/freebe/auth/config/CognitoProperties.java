package com.foru.freebe.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "cognito")
@Data
public class CognitoProperties {
	private String clientId;
	private String clientSecret;
	private String clientName;
	private String userPoolId;
}

