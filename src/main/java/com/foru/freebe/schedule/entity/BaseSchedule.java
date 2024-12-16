package com.foru.freebe.schedule.entity;


import java.time.LocalTime;

import com.foru.freebe.common.entity.BaseEntity;
import com.foru.freebe.member.entity.Member;

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
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BaseSchedule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "base_schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photographer_id")
    @NotNull(message = "Photographer must not be null")
    private Member photographer;

    @NotNull(message = "DayOfWeek must not be null")
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time must not be null")
    private LocalTime startTime;

    @NotNull(message = "End time must not be null")
    private LocalTime endTime;

    @NotNull(message = "Operation status must not be null")
    @Enumerated(EnumType.STRING)
    private OperationStatus operationStatus;

    public void updateScheduleTime(LocalTime startTime, LocalTime endTime, OperationStatus operationStatus) {
        if (operationStatus == OperationStatus.INACTIVE) {
            this.startTime = LocalTime.of(9,0,0);
            this.endTime = LocalTime.of(18,0,0);
        } else {
            this.startTime = startTime;
            this.endTime = endTime;
        }
        this.operationStatus = operationStatus;
    }

    @Builder
    public BaseSchedule(Member photographer, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime,
        OperationStatus operationStatus) {
        this.photographer = photographer;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.operationStatus = operationStatus;
    }
}
