package com.foru.freebe.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.reservation.entity.ReservationForm;

public interface ReservationFormRepository extends JpaRepository<ReservationForm, Long> {
}
