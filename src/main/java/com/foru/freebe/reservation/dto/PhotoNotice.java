package com.foru.freebe.reservation.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PhotoNotice implements Serializable {

    @NotNull
    private String title;

    @NotNull
    private String content;
}
