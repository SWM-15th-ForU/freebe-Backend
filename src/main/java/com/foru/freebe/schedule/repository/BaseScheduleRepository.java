package com.foru.freebe.schedule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.schedule.entity.BaseSchedule;
import com.foru.freebe.schedule.entity.DayOfWeek;

public interface BaseScheduleRepository extends JpaRepository<BaseSchedule, Long> {
    List<BaseSchedule> findByPhotographerId(Long photographerId);

    BaseSchedule findByDayOfWeekAndPhotographerId(DayOfWeek dayOfWeek, Long photographerId);
}
