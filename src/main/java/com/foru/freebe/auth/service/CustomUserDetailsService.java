package com.foru.freebe.auth.service;

import java.util.Optional;

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
	public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Long kakaoId = Long.valueOf(username);
		Optional<Member> member = memberRepository.findByKakaoId(kakaoId);

		if (member.isPresent()) {
			return new CustomUserDetails(member.get());
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}
}

