package com.foru.freebe.profile.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.foru.freebe.profile.entity.ApiResponseDto;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
	private final ProfileRepository profileRepository;

	private static final String BASE_URL = "https://freebe.co.kr/photographer/";

	@Override
	public ApiResponseDto<String> registerUniqueUrl() {
		String uniqueUrl = generateUniqueUrl();

		Profile memberProfile = Profile.builder()
			.uniqueUrl(uniqueUrl)
			.introductionContent(null)
			.bannerImageUrl(null)
			.build();

		profileRepository.save(memberProfile);

		return ApiResponseDto.<String>builder()
			.status(HttpStatus.CREATED)
			.message("Successfully registered")
			.data(uniqueUrl)
			.build();
	}

	private String generateUniqueUrl() {
		String uniqueId = UUID.randomUUID().toString();
		return BASE_URL + uniqueId;
	}
}
