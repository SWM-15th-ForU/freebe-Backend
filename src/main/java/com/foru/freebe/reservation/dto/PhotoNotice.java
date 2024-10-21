package com.foru.freebe.reservation.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PhotoNotice implements Serializable {

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
