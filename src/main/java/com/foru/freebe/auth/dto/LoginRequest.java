package com.foru.freebe.auth.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String code;
    private String roleType;
}
