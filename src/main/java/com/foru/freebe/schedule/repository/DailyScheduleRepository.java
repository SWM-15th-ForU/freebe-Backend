package com.foru.freebe.schedule.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foru.freebe.member.entity.Member;
import com.foru.freebe.schedule.entity.DailySchedule;
import com.foru.freebe.schedule.entity.ScheduleStatus;

public interface DailyScheduleRepository extends JpaRepository<DailySchedule, Long> {
    Optional<DailySchedule> findByMemberAndId(Member member, Long scheduleId);

    List<DailySchedule> findByMember(Member member);

    @Query("SELECT ds FROM DailySchedule ds WHERE ds.member = :photographer "
        + "AND ds.date = :date "
        + "AND ((ds.startTime < :endTime AND ds.endTime > :startTime))"
        + "AND ds.scheduleStatus IN (:scheduleStatuses)")
    List<DailySchedule> findConflictingSchedulesByStatuses(Member photographer, LocalDate date, LocalTime startTime,
        LocalTime endTime, List<ScheduleStatus> scheduleStatuses);

    @Query("SELECT ds FROM DailySchedule ds WHERE ds.date = :date AND ds.member = :member "
        + "AND ds.scheduleStatus IN (:statuses) ORDER BY ds.startTime ASC")
    List<DailySchedule> findByMemberAndStatusesOrderByStartTime(LocalDate date, Member member,
        List<ScheduleStatus> statuses);
}
