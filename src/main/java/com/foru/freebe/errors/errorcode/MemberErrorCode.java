package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    INVALID_LOGIN_REQUEST(400, "Invalid login request"),
    MEMBER_NOT_FOUND(404, "Member not found");

    private final int httpStatus;
    private final String message;
}
