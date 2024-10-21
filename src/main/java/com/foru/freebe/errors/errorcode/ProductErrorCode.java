package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    INVALID_ACTIVE_STATUS(400, "ActiveStatus must be different current stored ActiveStatus"),
    PRODUCT_INACTIVE_STATUS(400, "The product is currently inactive, so you can't register a booking form with it"),
    PRODUCT_ALREADY_EXISTS(400, "The product already exists, so it cannot be registered again"),
    COMPONENT_TITLE_ALREADY_EXISTS(400, "Component title already exists, so it cannot be registered again"),
    INVALID_FILE_NAME(400, "The url or filename in the json file is incorrect"),
    INVALID_PRODUCT_TITLE(400, "The product title is incorrect"),
    PRODUCT_NOT_FOUND(404, "The product could not be found");

    private final int httpStatus;
    private final String message;
}
