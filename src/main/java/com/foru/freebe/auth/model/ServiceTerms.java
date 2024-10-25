package com.foru.freebe.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ServiceTerms {
    @JsonProperty("tag")
    private String tag;

    @JsonProperty("agreed")
    private Boolean agreed;
}
