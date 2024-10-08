package com.foru.freebe.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LinkErrorCode implements ErrorCode {

    DUPLICATE_TITLE(400, "Title is duplicated"),
    DUPLICATE_URL(400, "Url is duplicated");

    private final int httpStatus;
    private final String message;
}
