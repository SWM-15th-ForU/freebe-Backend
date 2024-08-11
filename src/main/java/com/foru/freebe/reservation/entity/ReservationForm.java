package com.foru.freebe.reservation.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.Type;

import com.foru.freebe.member.entity.Member;

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
public class ReservationForm {
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

    @NotNull(message = "Total price must not be null")
    @Positive
    private Long totalPrice;

    @NotNull(message = "Service term agreement must not be null")
    private Boolean serviceTermAgreement;

    @NotNull(message = "Photographer term agreement must not be null")
    private Boolean photographerTermAgreement;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Reservation status must not be null")
    private ReservationStatus reservationStatus;

    @Type(JsonType.class)
    @Column(name = "photo_info", columnDefinition = "longtext")
    private Map<String, String> photoInfo = new HashMap<>();

    @Type(JsonType.class)
    @Column(name = "photo_schedule", columnDefinition = "longtext")
    private Map<Integer, LocalDateTime> photoSchedule = new HashMap<>();

    private String requestMemo;

    private String photographerMemo;

    @Builder
    public ReservationForm(Member photographer, Member customer, String instagramId, String productTitle,
        Long totalPrice, Boolean serviceTermAgreement, Boolean photographerTermAgreement,
        ReservationStatus reservationStatus, Map<String, String> photoInfo, Map<Integer, LocalDateTime> photoSchedule,
        String requestMemo, String photographerMemo) {
        this.photographer = photographer;
        this.customer = customer;
        this.instagramId = instagramId;
        this.productTitle = productTitle;
        this.totalPrice = totalPrice;
        this.serviceTermAgreement = serviceTermAgreement;
        this.photographerTermAgreement = photographerTermAgreement;
        this.reservationStatus = reservationStatus;
        this.photoInfo = photoInfo;
        this.photoSchedule = photoSchedule;
        this.requestMemo = requestMemo;
        this.photographerMemo = photographerMemo;
    }

    public static ReservationFormBuilder builder(Member photographer, Member customer, String instagramId,
        String productTitle, Long totalPrice, Boolean serviceTermAgreement, Boolean photographerTermAgreement,
        ReservationStatus reservationStatus) {
        return new ReservationFormBuilder()
            .photographer(photographer)
            .customer(customer)
            .instagramId(instagramId)
            .productTitle(productTitle)
            .totalPrice(totalPrice)
            .serviceTermAgreement(serviceTermAgreement)
            .photographerTermAgreement(photographerTermAgreement)
            .reservationStatus(reservationStatus);
    }
}
