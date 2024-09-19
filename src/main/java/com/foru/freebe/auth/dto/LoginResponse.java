package com.foru.freebe.auth.dto;

import com.foru.freebe.jwt.model.JwtTokenModel;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {
    private JwtTokenModel token;
    private String uniqueUrl;
    private String message;

    @Builder
    public LoginResponse(JwtTokenModel token, String uniqueUrl, String message) {
        this.token = token;
        this.uniqueUrl = uniqueUrl;
        this.message = message;
    }
}
