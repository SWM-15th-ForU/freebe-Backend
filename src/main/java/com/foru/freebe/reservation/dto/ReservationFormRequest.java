package com.foru.freebe.reservation.dto;

import java.time.LocalDateTime;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationFormRequest {
    @NotNull
    private Long photographerId;
    @NotNull
    private String instagramId;
    @NotNull
    private String productTitle;
    private Map<String, String> photoInfo;
    @NotNull
    private Map<Integer, LocalDateTime> photoSchedule;
    private String requestMemo;
    @NotNull
    private Long totalPrice;
    @NotNull
    private Boolean serviceTermAgreement;
    @NotNull
    private Boolean photographerTermAgreement;
}
