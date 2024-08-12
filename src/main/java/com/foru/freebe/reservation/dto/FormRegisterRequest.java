package com.foru.freebe.reservation.dto;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FormRegisterRequest {
    private Long photographerId;

    @NotNull
    private String instagramId;

    @NotNull
    private String productTitle;

    private Map<String, String> photoInfo;

    private Map<Integer, PreferredDate> preferredDates;

    private List<String> preferredImages;

    private String customerMemo;

    @NotNull
    private Long totalPrice;

    @NotNull
    private Boolean serviceTermAgreement;

    @NotNull
    private Boolean photographerTermAgreement;
}
