package com.foru.freebe.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.entity.ReservationStatus;

public interface ReservationFormRepository extends JpaRepository<ReservationForm, Long> {
    List<ReservationForm> findAllByPhotographerId(Long photographerId);

    Optional<ReservationForm> findByPhotographerIdAndId(Long photographerId, Long id);

    Optional<ReservationForm> findByCustomerIdAndId(Long customerId, Long id);

    List<ReservationForm> findAllByPhotographerIdAndProductTitle(Long photographerId, String productTitle);

    Page<ReservationForm> findByPhotographerId(Long photographerId, Pageable pageable);

    Page<ReservationForm> findByPhotographerIdAndReservationStatusIn(Long photographerId,
        List<ReservationStatus> status, Pageable pageable);

    Page<ReservationForm> findByPhotographerIdAndShootingDate_DateBetween(Long photographerId, LocalDate from,
        LocalDate to,
        Pageable pageable);

    Page<ReservationForm> findByPhotographerIdAndReservationStatusInAndShootingDate_DateBetween(Long photographerId,
        List<ReservationStatus> status, LocalDate from, LocalDate to, Pageable pageable);

    List<ReservationForm> findByProductTitle(String title);
}
