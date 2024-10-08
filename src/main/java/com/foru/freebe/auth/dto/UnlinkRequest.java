package com.foru.freebe.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UnlinkRequest {
    @NotNull
    private String reason;
}
