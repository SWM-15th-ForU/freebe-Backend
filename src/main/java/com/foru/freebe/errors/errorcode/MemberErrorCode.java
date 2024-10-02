package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    INVALID_LOGIN_REQUEST(400, "Invalid login request");

    private final int httpStatus;
    private final String message;
}
