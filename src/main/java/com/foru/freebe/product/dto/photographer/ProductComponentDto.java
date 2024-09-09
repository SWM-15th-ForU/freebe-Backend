package com.foru.freebe.product.dto.photographer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductComponentDto {
    @NotBlank
    @Size(max = 30, message = "Title cannot be longer than 30 characters")
    private String title;

    @NotBlank
    @Size(max = 100, message = "Content cannot be longer than 100 characters")
    private String content;

    @Size(max = 100, message = "Description cannot be longer than 100 characters")
    private String description;

    @Builder
    public ProductComponentDto(String title, String content, String description) {
        this.title = title;
        this.content = content;
        this.description = description;
    }
}
