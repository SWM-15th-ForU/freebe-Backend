package com.foru.freebe.notice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeDto {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @Builder
    public NoticeDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
