package com.foru.freebe.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageSendResponse {
    @JsonProperty("code")
    String code;

    @JsonProperty("data")
    DataResponse data;

    @JsonProperty("message")
    String message;

    @JsonProperty("originMessage")
    String originMessage;
}
