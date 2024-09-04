package com.foru.freebe.reservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.reservation.entity.ReferenceImage;
import com.foru.freebe.reservation.entity.ReservationForm;

public interface ReferenceImageRepository extends JpaRepository<ReferenceImage, Long> {
    List<ReferenceImage> findAllByReservationForm(ReservationForm reservationForm);
}
