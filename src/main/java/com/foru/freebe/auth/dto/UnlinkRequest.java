package com.foru.freebe.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UnlinkRequest {
    @NotBlank
    @Size(max = 500, message = "탈퇴사유는 최대 500자까지 입력 가능합니다.")
    private String reason;
}
