package com.foru.freebe.schedule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.member.entity.Member;
import com.foru.freebe.schedule.entity.BaseSchedule;
import com.foru.freebe.schedule.entity.OperationStatus;

import java.time.DayOfWeek;
import java.util.Optional;

public interface BaseScheduleRepository extends JpaRepository<BaseSchedule, Long> {
    List<BaseSchedule> findByPhotographerId(Long photographerId);

    BaseSchedule findByDayOfWeekAndPhotographerId(DayOfWeek dayOfWeek, Long photographerId);

    Optional<BaseSchedule> findByOperationStatusAndDayOfWeekAndPhotographer(OperationStatus operationStatus,
        DayOfWeek dayOfWeek, Member photographer);
}
