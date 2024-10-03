package com.foru.freebe.profile.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileRequest {
    private String introductionContent;
    private String existingBannerImageUrl;
    private String existingProfileImageUrl;
    private List<LinkInfo> linkInfos;

    @Builder
    public UpdateProfileRequest(String introductionContent, String existingBannerImageUrl,
        String existingProfileImageUrl, List<LinkInfo> linkInfos) {
        this.introductionContent = introductionContent;
        this.existingBannerImageUrl = existingBannerImageUrl;
        this.existingProfileImageUrl = existingProfileImageUrl;
        this.linkInfos = linkInfos;
    }
}
