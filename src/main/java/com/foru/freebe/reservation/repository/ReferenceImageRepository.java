package com.foru.freebe.reservation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.reservation.entity.ReferenceImage;
import com.foru.freebe.reservation.entity.ReservationForm;

public interface ReferenceImageRepository extends JpaRepository<ReferenceImage, Long> {
    Optional<List<ReferenceImage>> findAllByReservationForm(ReservationForm reservationForm);
}
