package com.foru.freebe.reservation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.entity.ReservationHistory;

public interface ReservationHistoryRepository extends JpaRepository<ReservationHistory, Long> {
    Optional<List<ReservationHistory>> findAllByReservationForm(ReservationForm reservationForm);
}
