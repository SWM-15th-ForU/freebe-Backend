package com.foru.freebe.baseSchedule.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.baseSchedule.entity.BaseSchedule;
import com.foru.freebe.baseSchedule.entity.DayOfWeek;

public interface BaseScheduleRepository extends JpaRepository<BaseSchedule, Long> {
    BaseSchedule findByDayOfWeekAndPhotographerId(DayOfWeek dayOfWeek, Long photographerId);
}
