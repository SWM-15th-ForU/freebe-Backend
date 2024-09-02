package com.foru.freebe.reservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class ReferenceImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_form_id")
    private ReservationForm reservationForm;

    @NotNull
    private String origin_url;

    @NotNull
    private String thumbnail_url;

    private ReferenceImage(String origin_url, String thumbnail_url, ReservationForm reservationForm) {
        this.origin_url = origin_url;
        this.thumbnail_url = thumbnail_url;
        this.reservationForm = reservationForm;
    }

    public static ReferenceImage updateReferenceImage(String origin_url, String thumbnail_url,
        ReservationForm reservationForm) {
        return new ReferenceImage(origin_url, thumbnail_url, reservationForm);
    }
}
