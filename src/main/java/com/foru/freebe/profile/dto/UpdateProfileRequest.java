package com.foru.freebe.profile.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileRequest {
    @NotBlank
    @Size(max = 100, message = "연락처는 최대 100자까지 입력 가능합니다.")
    private String contact;

    @Size(max = 500, message = "소개글은 최대 500자까지 입력 가능합니다.")
    private String introductionContent;

    private String existingBannerImageUrl;

    private String existingProfileImageUrl;

    private List<LinkInfo> linkInfos;

    @Builder
    public UpdateProfileRequest(String contact, String introductionContent, String existingBannerImageUrl,
        String existingProfileImageUrl, List<LinkInfo> linkInfos) {
        this.contact = contact;
        this.introductionContent = introductionContent;
        this.existingBannerImageUrl = existingBannerImageUrl;
        this.existingProfileImageUrl = existingProfileImageUrl;
        this.linkInfos = linkInfos;
    }
}
