package com.foru.freebe.auth.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.foru.freebe.auth.model.CustomUserDetails;
import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long id = Long.valueOf(username);
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + id));

        return new MemberAdapter(member);
    }
}

