package com.foru.freebe.profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.entity.ProfileImage;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage> findByProfile(Profile profile);
}
