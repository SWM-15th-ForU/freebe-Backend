package com.foru.freebe.message.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Button {
    @NotBlank
    private String name;

    @NotBlank
    private String type;

    private String urlPc;

    private String urlMobile;

    @Builder
    public Button(String name, String type, String urlPc, String urlMobile) {
        this.name = name;
        this.type = type;
        this.urlPc = urlPc;
        this.urlMobile = urlMobile;
    }
}
