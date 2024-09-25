package com.foru.freebe.auth.dto;

import com.foru.freebe.jwt.model.JwtTokenModel;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {
    private JwtTokenModel token;
    private String profileName;
    private String message;

    @Builder
    public LoginResponse(JwtTokenModel token, String profileName, String message) {
        this.token = token;
        this.profileName = profileName;
        this.message = message;
    }
}
