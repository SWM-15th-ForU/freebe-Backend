package com.foru.freebe.common.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageLinkSet {
    private List<String> originUrl;
    private List<String> thumbnailUrl;

    public String getFirstOriginUrl() {
        return originUrl.get(0);
    }

    public String getFirstThumbnailUrl() {
        return thumbnailUrl.get(0);
    }
}
