package com.foru.freebe.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SingleImageLink {
    private String originalUrl;
    private String thumbnailUrl;
}
