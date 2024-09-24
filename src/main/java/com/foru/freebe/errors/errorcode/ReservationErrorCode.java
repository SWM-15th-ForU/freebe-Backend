package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode implements ErrorCode {
    INVALID_RESERVATION_STATUS_FOR_CANCELLATION(400, "예약 취소가 불가능한 상태입니다");

    private final int httpStatus;
    private final String message;
}
