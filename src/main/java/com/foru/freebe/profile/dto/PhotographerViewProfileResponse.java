package com.foru.freebe.profile.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PhotographerViewProfileResponse {
    private String bannerImageUrl;
    private String profileImageUrl;
    private String profileName;
    private String contact;
    private String introductionContent;
    private List<LinkInfo> linkInfos;

    @Builder
    public PhotographerViewProfileResponse(String bannerImageUrl, String profileImageUrl, String profileName,
        String contact, String introductionContent, List<LinkInfo> linkInfos) {
        this.bannerImageUrl = bannerImageUrl;
        this.profileImageUrl = profileImageUrl;
        this.profileName = profileName;
        this.contact = contact;
        this.introductionContent = introductionContent;
        this.linkInfos = linkInfos;
    }
}
