package com.foru.freebe.profile.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ResponseBody;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.profile.dto.PhotographerViewProfileResponse;
import com.foru.freebe.profile.dto.UpdateProfileRequest;
import com.foru.freebe.profile.service.PhotographerProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photographer")
public class PhotographerProfileController {
    private final PhotographerProfileService photographerProfileService;

    @GetMapping("/profile")
    public ResponseEntity<ResponseBody<PhotographerViewProfileResponse>> getCurrentProfile(
        @AuthenticationPrincipal MemberAdapter memberAdapter) {

        Member photographer = memberAdapter.getMember();
        PhotographerViewProfileResponse responseData = photographerProfileService.getMyCurrentProfile(photographer);

        ResponseBody<PhotographerViewProfileResponse> responseBody = ResponseBody.<PhotographerViewProfileResponse>builder()
            .message("Good Response")
            .data(responseData).build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @PutMapping("/profile")
    public ResponseEntity<ResponseBody<Void>> updateProfile(
        @AuthenticationPrincipal MemberAdapter memberAdapter,
        @Valid @RequestPart(value = "request") UpdateProfileRequest request,
        @RequestPart(value = "bannerImage", required = false) MultipartFile bannerImage,
        @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {

        Member photographer = memberAdapter.getMember();
        photographerProfileService.updateProfile(photographer, request, bannerImage, profileImage);

        ResponseBody<Void> responseBody = ResponseBody.<Void>builder()
            .message("Updated successfully")
            .data(null).build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }
}
