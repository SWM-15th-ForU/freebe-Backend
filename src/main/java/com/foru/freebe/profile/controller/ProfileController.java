package com.foru.freebe.profile.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.profile.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profiles")
public class ProfileController {
    private final ProfileService profileService;

    // Url 생성 테스트를 위한 API
    // TODO 회원가입 로직 뒤에 해당 API관련 서비스로직이 추가되면 삭제 요망
    @PostMapping("/uniqueUrl")
    public ApiResponse<String> registerUniqueUrl() {
        return profileService.registerUniqueUrl();
    }
}
