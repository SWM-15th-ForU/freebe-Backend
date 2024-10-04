package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode implements ErrorCode {
    INVALID_STATUS_TRANSITION(400, "상태 전환이 불가능합니다"),
    NO_RESERVATION_FORM(400, "해당하는 예약서가 존재하지 않습니다"),
    INVALID_SHOOTING_DATE(400, "해당 날짜는 확정일자로 등록할 수 없습니다"),
    INVALID_SHOOTING_TIME(400, "해당 촬영시간은 확정일자로 등록할 수 없습니다"),
    INVALID_RESERVATION_STATUS(400, "현재 예약서 상태에서는 확정일자를 등록할 수 없습니다");

    private final int httpStatus;
    private final String message;
}
