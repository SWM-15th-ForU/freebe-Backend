package com.foru.freebe.profile.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;

    private static final String BASE_URL = "https://freebe.co.kr/photographer/";

    public ApiResponse<String> registerUniqueUrl() {
        String uniqueUrl = generateUniqueUrl();

        Profile memberProfile = Profile.builder()
            .uniqueUrl(uniqueUrl)
            .introductionContent(null)
            .bannerImageUrl(null)
            .build();

        profileRepository.save(memberProfile);

        return ApiResponse.<String>builder()
            .status(200)
            .message("Successfully registered")
            .data(uniqueUrl)
            .build();
    }

    private String generateUniqueUrl() {
        String uniqueId = UUID.randomUUID().toString();
        return BASE_URL + uniqueId;
    }
}
