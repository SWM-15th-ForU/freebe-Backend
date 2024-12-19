package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorCode implements ErrorCode {

    START_TIME_AFTER_END_TIME(400, "시작시간과 종료시간이 올바르지 않습니다."),
    CANNOT_CHANGE_SAME_SCHEDULE_UNIT(400, "같은 스케줄 단위로는 변경할 수 없습니다.");


    private final int httpStatus;
    private final String message;
}
