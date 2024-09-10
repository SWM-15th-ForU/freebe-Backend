package com.foru.freebe.reservation.dto;

import java.time.LocalDate;

import com.foru.freebe.reservation.entity.ReservationStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FormComponent {
    @NotNull
    private Long reservationId;

    @NotNull
    private LocalDate reservationSubmissionDate;

    @NotNull
    private ReservationStatus reservationStatus;

    @NotBlank
    private String customerName;

    @NotBlank
    private String productTitle;

    @NotNull
    private PreferredDate shootingDate;

    @Builder
    public FormComponent(Long reservationId, LocalDate reservationSubmissionDate, ReservationStatus reservationStatus,
        String customerName, String productTitle, PreferredDate shootingDate) {
        this.reservationId = reservationId;
        this.reservationSubmissionDate = reservationSubmissionDate;
        this.reservationStatus = reservationStatus;
        this.customerName = customerName;
        this.productTitle = productTitle;
        this.shootingDate = shootingDate;
    }
}
