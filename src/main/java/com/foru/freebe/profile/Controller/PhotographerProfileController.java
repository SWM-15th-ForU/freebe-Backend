package com.foru.freebe.profile.Controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.profile.dto.ProfileResponse;
import com.foru.freebe.profile.dto.UpdateProfileRequest;
import com.foru.freebe.profile.service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photographer")
public class PhotographerProfileController {
    private final ProfileService profileService;

    @GetMapping("/profile")
    public ApiResponse<ProfileResponse> getCurrentProfile(@AuthenticationPrincipal MemberAdapter memberAdapter) {
        Member photographer = memberAdapter.getMember();
        return profileService.getCurrentProfile(photographer);
    }

    @PutMapping("/profile")
    public ApiResponse<Void> updateProfile(@AuthenticationPrincipal MemberAdapter memberAdapter,
        @Valid @RequestBody UpdateProfileRequest updateRequest) {
        Member photographer = memberAdapter.getMember();
        return profileService.updateProfile(updateRequest, photographer);
    }
}
