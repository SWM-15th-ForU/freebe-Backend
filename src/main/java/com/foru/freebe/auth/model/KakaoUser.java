package com.foru.freebe.auth.model;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUser {
    @JsonProperty("id")
    private Long kakaoId;

    @JsonProperty("connected_at")
    private LocalDateTime connectedAt;

    @JsonProperty("kakao_account")
    private Map<String, Object> kakaoAccount;

    public String getUserName() {
        return (String)kakaoAccount.get("name");
    }

    public String getEmail() {
        return (String)kakaoAccount.get("email");
    }

    public String getPhoneNumber() {
        String phoneNumber = (String)kakaoAccount.get("phone_number");
        return "0" + phoneNumber.replace("+82 ", "");
    }

    public Integer getBirthYear() {
        return (Integer)kakaoAccount.get("birth_year");
    }

    public String getGender() {
        return (String)kakaoAccount.get("gender");
    }
}
