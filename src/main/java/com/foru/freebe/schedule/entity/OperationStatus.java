package com.foru.freebe.schedule.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OperationStatus {

    @JsonProperty("ACTIVE")
    ACTIVE,
    @JsonProperty("INACTIVE")
    INACTIVE
}
