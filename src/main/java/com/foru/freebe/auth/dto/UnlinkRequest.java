package com.foru.freebe.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UnlinkRequest {
    @NotBlank
    private String reason;
}
