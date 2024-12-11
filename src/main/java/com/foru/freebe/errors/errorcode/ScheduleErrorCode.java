package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorCode implements ErrorCode {

    START_TIME_AFTER_END_TIME(400, "시작시간이 종료시간보다 더 늦습니다.");

    private final int httpStatus;
    private final String message;
}
