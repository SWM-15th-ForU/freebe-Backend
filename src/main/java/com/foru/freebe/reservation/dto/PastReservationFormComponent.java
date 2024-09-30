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
public class PastReservationFormComponent {
    @NotNull
    private ReservationStatus reservationStatus;

    @NotNull
    private Long reservationId;

    @NotNull
    private LocalDate reservationSubmissionDate;

    @NotBlank
    private String customerName;

    @NotBlank
    private String productTitle;

    @NotNull
    private PreferredDate shootingDate;

    @NotBlank
    private Long price;

    @NotBlank
    private String image;

    @Builder
    public PastReservationFormComponent(ReservationStatus reservationStatus, Long reservationId,
        LocalDate reservationSubmissionDate, String customerName, String productTitle, PreferredDate shootingDate,
        Long price, String image) {
        this.reservationStatus = reservationStatus;
        this.reservationId = reservationId;
        this.reservationSubmissionDate = reservationSubmissionDate;
        this.customerName = customerName;
        this.productTitle = productTitle;
        this.shootingDate = shootingDate;
        this.price = price;
        this.image = image;
    }
}
