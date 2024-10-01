package com.foru.freebe.common.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageLinkSet {
    private List<String> originUrls;
    private List<String> thumbnailUrls;
}
