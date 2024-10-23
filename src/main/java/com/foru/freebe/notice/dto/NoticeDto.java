package com.foru.freebe.notice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeDto {
    @NotBlank
    @Size(max = 30, message = "공지사항 제목은 최대 30자까지 입력 가능합니다.")
    private String title;

    @NotBlank
    @Size(max = 1000, message = "공지사항 내용은 최대 1000자까지 입력 가능합니다.")
    private String content;

    @Builder
    public NoticeDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
