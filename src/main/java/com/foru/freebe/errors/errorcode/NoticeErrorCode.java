package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NoticeErrorCode implements ErrorCode {

    NOTICE_TITLE_DUPLICATE(400, "중복된 제목이 존재합니다"),
    NOT_FOUND_ESSENTIAL_TITLE(400, "필수 규정이 포함되어 있지 않습니다");

    private final int httpStatus;
    private final String message;
}
