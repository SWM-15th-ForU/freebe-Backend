package com.foru.freebe.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.member.entity.DeletedMember;

public interface DeletedMemberRepository extends JpaRepository<DeletedMember, Long> {
}
