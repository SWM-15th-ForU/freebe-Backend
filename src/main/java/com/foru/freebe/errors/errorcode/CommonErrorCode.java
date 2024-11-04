package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    INVALID_PARAMETER(400, "Invalid parameter included"),
    ACCESS_DENIED(403, "You don't have permission to access this resource."),
    RESOURCE_NOT_FOUND(404, "Resource not exists"),
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    IO_EXCEPTION(500, "IoException occurred"),
    MAXIMUM_UPLOAD_SIZE_EXCEEDED(500, "Maximum upload size exceeded");

    private final int httpStatus;
    private final String message;
}
