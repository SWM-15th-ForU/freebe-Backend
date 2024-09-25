package com.foru.freebe.profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.member.entity.Member;
import com.foru.freebe.profile.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    boolean existsByMemberId(Long memberId);

    boolean existsByProfileName(String profileName);

    Optional<Profile> findByMemberId(Long memberId);

    Optional<Profile> findByMember(Member member);

    Optional<Profile> findByProfileName(String profileName);
}
