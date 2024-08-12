package com.foru.freebe.reservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReferenceImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_form_id")
    private ReservationForm reservationForm;

    private String referencingImage;

    private ReferenceImage(String referencingImage, ReservationForm reservationForm) {
        this.referencingImage = referencingImage;
        this.reservationForm = reservationForm;
    }

    public static ReferenceImage updateReferenceImage(String referencingImages, ReservationForm reservationForm) {
        return new ReferenceImage(referencingImages, reservationForm);
    }
}
