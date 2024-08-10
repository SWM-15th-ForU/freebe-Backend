package com.foru.freebe.reservation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.reservation.entity.ReservationForm;

public interface ReservationFormRepository extends JpaRepository<ReservationForm, Long> {
    Optional<List<ReservationForm>> findAllByPhotographerId(Long photographerId);
}
