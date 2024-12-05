package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorCode implements ErrorCode {
    DAILY_SCHEDULE_NOT_FOUND(500, "해당하는 날짜별 스케줄을 찾을 수 없습니다."),
    DAILY_SCHEDULE_OVERLAP(400, "해당 일정에 이미 등록된 스케줄이 있습니다."),
    DAILY_SCHEDULE_IN_PAST(400, "현재 시점 이전의 스케줄은 등록할 수 없습니다.");

    private final int httpStatus;
    private final String message;
}