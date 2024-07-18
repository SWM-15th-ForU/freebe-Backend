package com.foru.freebe.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByKakaoId(Long kakaoId);
}
