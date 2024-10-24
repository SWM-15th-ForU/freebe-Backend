package com.foru.freebe.reservation.entity;

import java.util.Map;

import org.hibernate.annotations.Type;

import com.foru.freebe.common.entity.BaseEntity;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.reservation.dto.PhotoNotice;
import com.foru.freebe.reservation.dto.PhotoOption;
import com.foru.freebe.reservation.dto.TimeSlot;

import io.hypersistence.utils.hibernate.type.json.JsonType;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationForm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_form_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photographer_id")
    @NotNull(message = "Photographer must not be null")
    private Member photographer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @NotNull(message = "Customer must not be null")
    private Member customer;

    @NotBlank(message = "Instagram ID must not be blank")
    private String instagramId;

    @NotBlank(message = "Product title must not be blank")
    private String productTitle;

    @NotNull(message = "Basic price must not be null")
    private Long basicPrice;

    @NotBlank(message = "Basic place must not be null")
    private String basicPlace;

    @NotNull(message = "Total price must not be null")
    @Positive
    private Long totalPrice;

    @NotNull(message = "Notice agreement must not be null")
    private Boolean noticeAgreement;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Reservation status must not be null")
    private ReservationStatus reservationStatus;

    @Type(JsonType.class)
    @Column(name = "photo_info", columnDefinition = "longtext")
    @NotNull(message = "PhotoInfo must not be null")
    private Map<String, String> photoInfo;

    @Type(JsonType.class)
    @Column(name = "preferred_date", columnDefinition = "longtext")
    @NotNull(message = "Preferred Date must not be null")
    private Map<Integer, TimeSlot> preferredDate;

    private String preferredPlace;

    @Type(JsonType.class)
    @Column(name = "shooting_date", columnDefinition = "longtext")
    private TimeSlot shootingDate;

    private String shootingPlace;

    @Type(JsonType.class)
    @Column(name = "photo_option", columnDefinition = "longtext")
    private Map<Integer, PhotoOption> photoOption;

    @Type(JsonType.class)
    @Column(name = "photo_notice", columnDefinition = "longtext")
    private Map<String, PhotoNotice> photoNotice;

    private String customerMemo;

    private String photographerMemo;

    public void updatePhotographerMemo(String updateMemo) {
        this.photographerMemo = updateMemo;
    }

    public void changeReservationStatus(ReservationStatus updateStatus) {
        this.reservationStatus = updateStatus;
    }

    public void updateShootingInfo(TimeSlot newShootingDate, String newShootingPlace) {
        this.shootingDate = newShootingDate;
        this.shootingPlace = newShootingPlace;
    }

    @Builder
    public ReservationForm(Member photographer, Member customer, String instagramId, String productTitle,
        Long basicPrice, String basicPlace, Long totalPrice, Boolean noticeAgreement,
        ReservationStatus reservationStatus, Map<String, String> photoInfo, Map<Integer, TimeSlot> preferredDate,
        String preferredPlace, Map<Integer, PhotoOption> photoOption, Map<String, PhotoNotice> photoNotice,
        String customerMemo, String photographerMemo) {
        this.photographer = photographer;
        this.customer = customer;
        this.instagramId = instagramId;
        this.productTitle = productTitle;
        this.basicPrice = basicPrice;
        this.basicPlace = basicPlace;
        this.totalPrice = totalPrice;
        this.noticeAgreement = noticeAgreement;
        this.reservationStatus = reservationStatus;
        this.photoInfo = photoInfo;
        this.preferredDate = preferredDate;
        this.preferredPlace = preferredPlace;
        this.photoOption = photoOption;
        this.photoNotice = photoNotice;
        this.customerMemo = customerMemo;
        this.photographerMemo = photographerMemo;
    }

    public static ReservationFormBuilder builder(Member photographer, Member customer, String instagramId,
        String productTitle, Long basicPrice, String basicPlace, Long totalPrice, Boolean noticeAgreement,
        ReservationStatus reservationStatus, Map<String, PhotoNotice> photoNotice) {
        return new ReservationFormBuilder()
            .photographer(photographer)
            .customer(customer)
            .instagramId(instagramId)
            .productTitle(productTitle)
            .basicPrice(basicPrice)
            .basicPlace(basicPlace)
            .totalPrice(totalPrice)
            .noticeAgreement(noticeAgreement)
            .reservationStatus(reservationStatus)
            .photoNotice(photoNotice);
    }
}
