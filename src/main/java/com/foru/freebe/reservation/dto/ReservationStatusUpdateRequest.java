package com.foru.freebe.reservation.dto;

import com.foru.freebe.reservation.entity.ReservationStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationStatusUpdateRequest {
    @NotNull
    private ReservationStatus updateStatus;

    private String cancellationReason;

    @Builder
    public ReservationStatusUpdateRequest(ReservationStatus updateStatus, String cancellationReason) {
        this.updateStatus = updateStatus;
        this.cancellationReason = cancellationReason;
    }
}