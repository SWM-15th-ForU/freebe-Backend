package com.foru.freebe.errors.errorcode;

public interface ErrorCode {
    String name();

    int getHttpStatus();

    String getMessage();
}
