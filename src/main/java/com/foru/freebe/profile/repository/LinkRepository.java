package com.foru.freebe.profile.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.profile.entity.Link;
import com.foru.freebe.profile.entity.Profile;

public interface LinkRepository extends JpaRepository<Link, Long> {
    List<Link> findByProfile(Profile profile);

    Optional<Link> findByTitle(String title);

    Optional<Link> findByUrl(String url);
}
