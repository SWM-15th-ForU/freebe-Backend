package com.foru.freebe.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.notice.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
