package com.foru.freebe.reservation.entity;

import com.foru.freebe.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReferenceImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_form_id")
    private ReservationForm reservationForm;

    @NotBlank
    private String originUrl;

    @NotBlank
    private String thumbnailUrl;

    private ReferenceImage(String originUrl, String thumbnailUrl, ReservationForm reservationForm) {
        this.originUrl = originUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.reservationForm = reservationForm;
    }

    public static ReferenceImage updateReferenceImage(String originUrl, String thumbnailUrl,
        ReservationForm reservationForm) {
        return new ReferenceImage(originUrl, thumbnailUrl, reservationForm);
    }
}
