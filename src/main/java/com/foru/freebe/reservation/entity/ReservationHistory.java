package com.foru.freebe.reservation.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_form_id")
    private ReservationForm reservationForm;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Reservation Status must not be null")
    private ReservationStatus reservationStatus;

    @CreationTimestamp
    private LocalDateTime statusUpdateDate;

    private ReservationHistory(ReservationForm reservationForm, ReservationStatus reservationStatus) {
        this.reservationForm = reservationForm;
        this.reservationStatus = reservationStatus;
    }

    public static ReservationHistory createReservationHistory(ReservationForm reservationForm,
        ReservationStatus reservationStatus) {
        return new ReservationHistory(reservationForm, reservationStatus);
    }
}
