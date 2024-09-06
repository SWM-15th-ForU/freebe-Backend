package com.foru.freebe.auth.dto;

import com.foru.freebe.member.entity.Role;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String code;
    private Role roleType;
}
