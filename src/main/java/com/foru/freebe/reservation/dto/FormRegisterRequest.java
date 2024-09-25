package com.foru.freebe.reservation.dto;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FormRegisterRequest {
    @NotBlank(message = "Profile name must not be blank")
    private String profileName;

    @NotBlank(message = "Instagram ID must not be blank")
    private String instagramId;

    @NotBlank(message = "Product title must not be blank")
    private String productTitle;

    private Map<String, String> photoInfo;

    @NotNull
    private Map<Integer, PreferredDate> preferredDates;

    private Map<Integer, PhotoOption> photoOptions;

    @Size(max = 300, message = "Memo cannot be longer than 300 characters")
    private String customerMemo;

    @NotNull
    private Long totalPrice;

    @NotNull
    private Boolean serviceTermAgreement;

    @NotNull
    private Boolean photographerTermAgreement;
}
