package com.foru.freebe.reservation.dto;

import com.foru.freebe.reservation.entity.ReservationStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationStatusUpdateRequest {
    @NotNull
    private ReservationStatus updateStatus;

    @Size(max = 100, message = "취소사유는 최대 100자까지 입력 가능합니다.")
    private String cancellationReason;
}