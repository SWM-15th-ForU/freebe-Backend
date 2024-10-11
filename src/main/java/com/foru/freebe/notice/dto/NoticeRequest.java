package com.foru.freebe.notice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
