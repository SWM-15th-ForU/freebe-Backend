package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    NO_UNLINK_REASON(400, "Reason for leaving is essential");

    private final int httpStatus;
    private final String message;
}
