package com.foru.freebe.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.profile.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
