package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    INVALID_ACTIVE_STATUS(400, "ActiveStatus must be different current stored ActiveStatus");

    private final int httpStatus;
    private final String message;
}
