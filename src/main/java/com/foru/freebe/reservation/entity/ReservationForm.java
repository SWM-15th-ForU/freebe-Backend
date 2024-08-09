package com.foru.freebe.reservation.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.Type;

import com.foru.freebe.member.entity.Member;

import io.hypersistence.utils.hibernate.type.json.JsonType;
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
    private Member photographer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Member customer;

    @NotNull
    private String instagramId;

    @NotNull
    private String productTitle;

    @Type(JsonType.class)
    @Column(name = "photo_info", columnDefinition = "longtext")
    private Map<String, String> photoInfo = new HashMap<>();

    @Type(JsonType.class)
    @Column(name = "photo_schedule", columnDefinition = "longtext")
    private Map<Integer, LocalDateTime> photoSchedule = new HashMap<>();

    private String requestMemo;

    private String photographerMemo;

    @NotNull
    private Long totalPrice;

    @NotNull
    private Boolean serviceTermAgreement;

    @NotNull
    private Boolean photographerTermAgreement;

    @NotNull
    private ReservationStatus reservationStatus;

    @Builder
    public ReservationForm(Member photographer, Member customer, String instagramId, String productTitle,
        Map<String, String> photoInfo, Map<Integer, LocalDateTime> photoSchedule, String requestMemo,
        String photographerMemo, Long totalPrice, Boolean serviceTermAgreement, Boolean photographerTermAgreement,
        ReservationStatus reservationStatus) {
        this.photographer = photographer;
        this.customer = customer;
        this.instagramId = instagramId;
        this.productTitle = productTitle;
        this.photoInfo = photoInfo;
        this.photoSchedule = photoSchedule;
        this.requestMemo = requestMemo;
        this.photographerMemo = photographerMemo;
        this.totalPrice = totalPrice;
        this.serviceTermAgreement = serviceTermAgreement;
        this.photographerTermAgreement = photographerTermAgreement;
        this.reservationStatus = reservationStatus;
    }
}
