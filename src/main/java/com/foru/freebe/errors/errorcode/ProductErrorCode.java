package com.foru.freebe.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    INVALID_ACTIVE_STATUS(HttpStatus.BAD_REQUEST, "ActiveStatus must be different current stored ActiveStatus");

    private final HttpStatus httpStatus;
    private final String message;
}
