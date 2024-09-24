package com.foru.freebe.reservation.entity;

import java.util.Arrays;

public enum ReservationStatusTransition {
    NEW(ReservationStatus.IN_PROGRESS, ReservationStatus.CANCELLED_BY_PHOTOGRAPHER,
        ReservationStatus.CANCELLED_BY_CUSTOMER),
    IN_PROGRESS(ReservationStatus.WAITING_FOR_DEPOSIT, ReservationStatus.CANCELLED_BY_PHOTOGRAPHER),
    WAITING_FOR_DEPOSIT(ReservationStatus.WAITING_FOR_PHOTO, ReservationStatus.CANCELLED_BY_PHOTOGRAPHER),
    WAITING_FOR_PHOTO(ReservationStatus.PHOTO_COMPLETED, ReservationStatus.CANCELLED_BY_PHOTOGRAPHER);

    private final ReservationStatus[] nextStatuses;

    ReservationStatusTransition(ReservationStatus... nextStatuses) {
        this.nextStatuses = nextStatuses;
    }

    public boolean isInvalidTransition(ReservationStatus targetStatus) {
        return !canTransitionTo(targetStatus);
    }

    private boolean canTransitionTo(ReservationStatus targetStatus) {
        return Arrays.asList(nextStatuses).contains(targetStatus);
    }
}
