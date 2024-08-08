package com.foru.freebe.jwt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.jwt.model.JwtTokenEntity;

public interface JwtTokenRepository extends JpaRepository<JwtTokenEntity, Long> {
    Optional<List<JwtTokenEntity>> findByMemberId(Long memberId);
}
