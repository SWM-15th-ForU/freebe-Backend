package com.foru.freebe.schedule.entity;

import java.time.LocalDate;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailySchedule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "ScheduleStatus must not be null")
    private ScheduleStatus scheduleStatus;

    @NotNull(message = "Date must not be null")
    private LocalDate date;

    @NotNull(message = "Start time must not be null")
    private LocalTime startTime;

    @NotNull(message = "End time must not be null")
    private LocalTime endTime;

    @Builder
    public DailySchedule(Member member, ScheduleStatus scheduleStatus, LocalDate date, LocalTime startTime,
        LocalTime endTime) {
        this.member = member;
        this.scheduleStatus = scheduleStatus;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void updateScheduleStatus(ScheduleStatus newScheduleStatus) {
        this.scheduleStatus = newScheduleStatus;
    }

    public void updateDate(LocalDate date) {
        this.date = date;
    }

    public void updateStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void updateEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
