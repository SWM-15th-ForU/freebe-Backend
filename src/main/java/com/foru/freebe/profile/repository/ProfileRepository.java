package com.foru.freebe.profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.member.entity.Member;
import com.foru.freebe.profile.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Boolean existsByMemberId(Long memberId);

    Optional<Profile> findByMember(Member member);

    Optional<Profile> findByUniqueUrl(String uniqueUrl);
}
