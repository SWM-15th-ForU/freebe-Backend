package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AwsErrorCode implements ErrorCode {
    AMAZON_S3_EXCEPTION(404, "Amazon S3 exception"),
    AMAZON_SERVICE_EXCEPTION(500, "Amazon service exception");

    private final int httpStatus;
    private final String message;
}
