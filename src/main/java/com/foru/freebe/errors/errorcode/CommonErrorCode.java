package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    INVALID_PARAMETER(400, "Invalid parameter included"),
    RESOURCE_NOT_FOUND(404, "Resource not exists"),
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    IO_EXCEPTION(500, "IoException occurred");

    private final int httpStatus;
    private final String message;
}
