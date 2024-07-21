package com.foru.freebe.profile.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.profile.entity.ApiResponseDto;
import com.foru.freebe.profile.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profiles")
public class ProfileController {
	private final ProfileService profileService;

	@PostMapping("/uniqueUrl")
	public ApiResponseDto<String> registerUniqueUrl() {
		return profileService.registerUniqueUrl();
	}
}
