package com.foru.freebe.notice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.notice.entity.Notice;
import com.foru.freebe.profile.entity.Profile;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByProfile(Profile profile);
}
