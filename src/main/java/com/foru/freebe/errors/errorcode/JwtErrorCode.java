package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {
    EXPIRED_TOKEN(401, "Expired token"),
    INVALID_TOKEN(400, "Invalid token");

    private final int httpStatus;
    private final String message;
}
