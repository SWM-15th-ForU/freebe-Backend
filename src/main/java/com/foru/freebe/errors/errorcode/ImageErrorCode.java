package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements ErrorCode {

    PUT_OBJECT_EXCEPTION(500, "Failed to upload image to S3 due to an internal error");

    private final int httpStatus;
    private final String message;
}
