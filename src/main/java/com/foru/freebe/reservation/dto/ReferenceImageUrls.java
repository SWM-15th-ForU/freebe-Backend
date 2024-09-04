package com.foru.freebe.reservation.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReferenceImageUrls {
    private List<String> originalImage;
    private List<String> thumbnailImage;

    @Builder
    public ReferenceImageUrls(List<String> originalImage, List<String> thumbnailImage) {
        this.originalImage = originalImage;
        this.thumbnailImage = thumbnailImage;
    }
}
