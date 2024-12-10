package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorCode implements ErrorCode {

    INCORRECT_TIME(400, "시작시간과 종료시간이 올바르지 않습니다.");

    private final int httpStatus;
    private final String message;
}
