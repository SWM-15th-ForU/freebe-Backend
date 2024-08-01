package com.foru.freebe.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.auth.model.JwtTokenEntity;

public interface JwtTokenRepository extends JpaRepository<JwtTokenEntity, Long> {
	Optional<JwtTokenEntity> findByKakaoId(Long kakaoId);
}

