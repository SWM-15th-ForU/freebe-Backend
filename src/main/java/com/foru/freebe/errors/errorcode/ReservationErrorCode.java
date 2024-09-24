package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode implements ErrorCode {
    INVALID_STATUS_TRANSITION(400, "상태 전환이 불가능합니다");

    private final int httpStatus;
    private final String message;
}
