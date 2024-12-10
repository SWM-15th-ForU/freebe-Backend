package com.foru.freebe.baseSchedule.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DayOfWeek {
    @JsonProperty("MONDAY")
    MONDAY,
    @JsonProperty("TUESDAY")
    TUESDAY,
    @JsonProperty("WEDNESDAY")
    WEDNESDAY,
    @JsonProperty("THURSDAY")
    THURSDAY,
    @JsonProperty("FRIDAY")
    FRIDAY,
    @JsonProperty("SATURDAY")
    SATURDAY,
    @JsonProperty("SUNDAY")
    SUNDAY;
}
