package com.foru.freebe.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PhotoOption {
    @NotBlank
    private String title;

    private Integer quantity;

    private Integer price;
}