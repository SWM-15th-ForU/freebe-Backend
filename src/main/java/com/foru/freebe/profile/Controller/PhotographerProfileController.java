package com.foru.freebe.profile.Controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.profile.dto.LinkInfo;
import com.foru.freebe.profile.dto.ProfileResponse;
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

    @PostMapping("/profile/link")
    public ApiResponse<Void> addExternalLink(@AuthenticationPrincipal MemberAdapter memberAdapter,
        @Valid @RequestBody LinkInfo request) {
        Member photographer = memberAdapter.getMember();
        return profileService.addExternalLink(request, photographer);
    }
}
