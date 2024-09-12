package com.foru.freebe.profile.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileRequest {
    private String bannerImageUrl;
    private String profileImageUrl;
    private String introductionContent;
    private List<LinkInfo> linkInfos;

    @Builder
    public UpdateProfileRequest(String bannerImageUrl, String profileImageUrl, String introductionContent,
        List<LinkInfo> linkInfos) {
        this.bannerImageUrl = bannerImageUrl;
        this.profileImageUrl = profileImageUrl;
        this.introductionContent = introductionContent;
        this.linkInfos = linkInfos;
    }
}
