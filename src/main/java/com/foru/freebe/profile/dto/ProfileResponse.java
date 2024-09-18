package com.foru.freebe.profile.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileResponse {
    private String bannerImageUrl;
    private String profileImageUrl;
    private String instagramId;
    private String introductionContent;
    private List<LinkInfo> linkInfos;

    @Builder
    public ProfileResponse(String bannerImageUrl, String profileImageUrl, String instagramId,
        String introductionContent,
        List<LinkInfo> linkInfos) {
        this.bannerImageUrl = bannerImageUrl;
        this.profileImageUrl = profileImageUrl;
        this.instagramId = instagramId;
        this.introductionContent = introductionContent;
        this.linkInfos = linkInfos;
    }
}
