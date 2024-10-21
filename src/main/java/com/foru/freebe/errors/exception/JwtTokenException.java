package com.foru.freebe.errors.exception;

import com.foru.freebe.errors.errorcode.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtTokenException extends RuntimeException {
    private final ErrorCode errorCode;

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }

    public int getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}
