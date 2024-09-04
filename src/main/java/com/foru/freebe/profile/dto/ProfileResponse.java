package com.foru.freebe.profile.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileResponse {
    private String bannerImageUrl;
    private String profileImageUrl;
    private String instagramId;
    private String introductionContent;
    private List<LinkInfo> linkInfos;
}
