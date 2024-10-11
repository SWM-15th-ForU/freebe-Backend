package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NoticeErrorCode implements ErrorCode {

    TITLE_DUPLICATE(400, "중복된 제목이 존재합니다");

    private final int httpStatus;
    private final String message;
}
