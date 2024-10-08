package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductImageErrorCode implements ErrorCode {
    PRODUCT_IMAGE_NOT_FOUND(404, "The product image could not be found");

    private final int httpStatus;
    private final String message;
}
