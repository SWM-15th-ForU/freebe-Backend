package com.foru.freebe.profile.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.profile.dto.ProfileResponse;
import com.foru.freebe.profile.service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerProfileController {
    private final ProfileService profileService;

    @GetMapping("/profile/{uniqueUrl}")
    public ApiResponse<ProfileResponse> getPhotographerProfile(
        @Valid @PathVariable("uniqueUrl") String uniqueUrl) {
        ProfileResponse profileResponse = profileService.getPhotographerProfile(uniqueUrl);

        return ApiResponse.<ProfileResponse>builder()
            .status(200)
            .message("Good Response")
            .data(profileResponse)
            .build();
    }
}