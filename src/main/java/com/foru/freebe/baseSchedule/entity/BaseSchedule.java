package com.foru.freebe.baseSchedule.entity;

import org.joda.time.DateTime;

import com.foru.freebe.common.entity.BaseEntity;
import com.foru.freebe.member.entity.Member;

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
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time must not be null")
    private DateTime startTime;

    @NotNull(message = "End time must not be null")
    private DateTime endTime;

    public void updateScheduleTime(DateTime startTime, DateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
