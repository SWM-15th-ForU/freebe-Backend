package com.foru.freebe.auth.service;

import java.util.Optional;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.member.service.MemberService;

@Service
public class KakaoUserRegistrationService {
	private final MemberRepository memberRepository;
	private final MemberService memberService;

	public KakaoUserRegistrationService(MemberRepository memberRepository, MemberService memberService) {
		this.memberRepository = memberRepository;
		this.memberService = memberService;
	}

	public void register(OAuth2User oAuth2User) {
		KakaoUser kakaoUser = new KakaoUser(oAuth2User);
		Optional<Member> member = memberRepository.findByKakaoId(kakaoUser.getName());

		if (member.isEmpty()) {
			memberService.register(kakaoUser);
		}
	}
}
