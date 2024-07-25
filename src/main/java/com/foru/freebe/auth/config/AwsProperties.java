package com.foru.freebe.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "aws")
@Data
public class AwsProperties {
	private String accessKeyId;
	private String secretAccessKey;
	private String region;
}