package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    INVALID_ACTIVE_STATUS(400, "ActiveStatus must be different current stored ActiveStatus"),
    PRODUCT_INACTIVE_STATUS(400, "The product is currently inactive, so you can't register a booking form with it");

    private final int httpStatus;
    private final String message;
}
