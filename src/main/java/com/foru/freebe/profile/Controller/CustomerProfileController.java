package com.foru.freebe.profile.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.common.dto.ResponseBody;
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
    public ResponseEntity<ResponseBody<ProfileResponse>> getPhotographerProfile(
        @Valid @PathVariable("uniqueUrl") String uniqueUrl) {

        ProfileResponse responseData = profileService.getPhotographerProfile(uniqueUrl);

        ResponseBody<ProfileResponse> responseBody = ResponseBody.<ProfileResponse>builder()
            .message("Good Response")
            .data(responseData)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }
}
