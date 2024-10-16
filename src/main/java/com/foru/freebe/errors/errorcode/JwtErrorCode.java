package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {
    INVALID_TOKEN(400, "Invalid token"),
    EXPIRED_TOKEN(401, "Expired token"),
    REVOKED_TOKEN(403, "Revoked token"),
    TOKEN_NOT_FOUND(404, "Token not found");

    private final int httpStatus;
    private final String message;
}
