package com.foru.freebe.member.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ScheduleUnit {
    @JsonProperty("THIRTY_MINUTES")
    THIRTY_MINUTES,
    @JsonProperty("SIXTY_MINUTES")
    SIXTY_MINUTES
}
