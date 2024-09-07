package com.foru.freebe.product.dto.photographer;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductComponentDto {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String description;

    @Builder
    public ProductComponentDto(String title, String content, String description) {
        this.title = title;
        this.content = content;
        this.description = description;
    }
}
