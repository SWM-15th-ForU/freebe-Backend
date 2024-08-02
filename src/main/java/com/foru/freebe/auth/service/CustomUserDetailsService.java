package com.foru.freebe.auth.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Long kakaoId = Long.valueOf(username);
		Optional<Member> member = memberRepository.findByKakaoId(kakaoId);

		//todo: null일때 예외처리
		return new CustomUserDetails(member.get());
	}
}

