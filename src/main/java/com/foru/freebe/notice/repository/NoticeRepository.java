package com.foru.freebe.notice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.member.entity.Member;
import com.foru.freebe.notice.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByMember(Member member);
}
