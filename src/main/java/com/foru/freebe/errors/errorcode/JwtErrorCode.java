package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {
    MISSING_TOKEN(400, "Missing token"),
    EXPIRED_ACCESS_TOKEN(401, "Access token has expired"),
    REVOKED_TOKEN(403, "This token has been revoked"),
    INVALID_TOKEN(403, "Invalid token");

    private final int httpStatus;
    private final String message;
}