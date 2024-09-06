package com.foru.freebe.reservation.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReservationStatus {
    @JsonProperty("NEW")
    NEW,

    @JsonProperty("IN_PROGRESS")
    IN_PROGRESS,

    @JsonProperty("WAITING_FOR_DEPOSIT")
    WAITING_FOR_DEPOSIT,

    @JsonProperty("WAITING_FOR_PHOTO")
    WAITING_FOR_PHOTO,

    @JsonProperty("PHOTO_COMPLETED")
    PHOTO_COMPLETED,

    @JsonProperty("CANCELLED")
    CANCELLED
}
