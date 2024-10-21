package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProfileErrorCode implements ErrorCode {
    PROFILE_NAME_ALREADY_EXISTS(400, "이미 존재하는 프로필명입니다."),
    PROFILE_NAME_NOT_FOUND(404, "존재하지 않는 프로필명입니다."),
    MEMBER_NOT_FOUND(404, "존재하지 않는 회원입니다.");

    private final int httpStatus;
    private final String message;
}
