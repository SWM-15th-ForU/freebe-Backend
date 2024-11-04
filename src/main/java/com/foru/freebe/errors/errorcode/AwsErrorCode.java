package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AwsErrorCode implements ErrorCode {
    AMAZON_S3_EXCEPTION(500, "Amazon S3 exception"),
    AMAZON_SERVICE_EXCEPTION(500, "Amazon service exception"),
    DELETE_OBJECT_EXCEPTION(500, "Failed to delete image to S3 due to an internal error"),
    MAXIMUM_UPLOAD_SIZE_EXCEEDED(500, "Maximum upload size exceeded");

    private final int httpStatus;
    private final String message;
}
