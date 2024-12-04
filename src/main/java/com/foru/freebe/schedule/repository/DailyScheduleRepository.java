package com.foru.freebe.schedule.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.foru.freebe.member.entity.Member;
import com.foru.freebe.schedule.entity.DailySchedule;

public interface DailyScheduleRepository extends JpaRepository<DailySchedule, Long> {
    Optional<DailySchedule> findByMemberAndId(Member member, Long scheduleId);

    List<DailySchedule> findByMember(Member member);

    @Query("SELECT ds FROM DailySchedule ds WHERE ds.member = :photographer "
        + "AND ((ds.startTime < :endTime AND ds.endTime > :startTime))")
    List<DailySchedule> findOverlappingSchedules(Member photographer, LocalTime startTime, LocalTime endTime);
}
