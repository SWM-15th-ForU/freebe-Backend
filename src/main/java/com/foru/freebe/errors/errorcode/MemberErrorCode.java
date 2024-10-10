package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    INVALID_LOGIN_REQUEST(400, "Invalid login request"),
    MEMBER_NOT_FOUND(400, "Member not found"),
    ERROR_MEMBER_LEAVING_FAILED(500, "Failed to complete the membership leaving. Please try again later.");

    private final int httpStatus;
    private final String message;
}
