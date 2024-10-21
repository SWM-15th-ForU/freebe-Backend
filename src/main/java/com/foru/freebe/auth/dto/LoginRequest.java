package com.foru.freebe.auth.dto;

import com.foru.freebe.member.entity.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LoginRequest {
    @NotBlank
    private String code;

    @NotNull
    private Role roleType;
}
