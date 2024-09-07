package com.foru.freebe.reservation.dto;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FormRegisterRequest {
    @NotNull
    private Long photographerId;

    @NotNull
    private String instagramId;

    @NotNull
    private String productTitle;

    private Map<String, String> photoInfo;

    @NotNull
    private Map<Integer, PreferredDate> preferredDates;

    private Map<Integer, PhotoOption> photoOptions;

    private String customerMemo;

    @NotNull
    private Long totalPrice;

    @NotNull
    private Boolean serviceTermAgreement;

    @NotNull
    private Boolean photographerTermAgreement;
}
