package com.foru.freebe.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtTokenModel {
    private String accessToken;
    private String refreshToken;
}