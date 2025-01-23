package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorCode implements ErrorCode {
    DAILY_SCHEDULE_NOT_FOUND(500, "해당하는 날짜별 스케줄을 찾을 수 없습니다."),
    DAILY_SCHEDULE_OVERLAP(400, "해당 일정에 이미 등록된 스케줄이 있습니다."),
    DAILY_SCHEDULE_IN_PAST(400, "현재 시점 이전의 스케줄은 등록할 수 없습니다."),
    START_TIME_AFTER_END_TIME(400, "시작시간과 종료시간이 올바르지 않습니다."),
    INVALID_SCHEDULE_UNIT(400, "기본스케줄 단위와 일치하지 않습니다.");

    private final int httpStatus;
    private final String message;
}