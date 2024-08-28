package com.foru.freebe.member.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.jwt.model.JwtTokenModel;
import com.foru.freebe.jwt.service.JwtService;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.Role;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.profile.service.ProfileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final ProfileService profileService;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    public ResponseEntity<ApiResponse<?>> findOrRegisterMember(KakaoUser kakaoUser, Role role) {
        Member member = memberRepository.findByKakaoId(kakaoUser.getKakaoId())
            .orElseGet(() -> {
                if (role == Role.PHOTOGRAPHER) {
                    return registerNewMember(kakaoUser, Role.PENDING);
                }
                return registerNewMember(kakaoUser, role);
            });

        ApiResponse<?> body = setResponseBody(member);

        JwtTokenModel token = jwtService.generateToken(member.getId());
        HttpHeaders headers = jwtService.setTokenHeaders(token);

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    private Member registerNewMember(KakaoUser kakaoUser, Role role) {
        Member newMember = Member.builder(kakaoUser.getKakaoId(), role, kakaoUser.getUserName(),
                kakaoUser.getEmail(), kakaoUser.getPhoneNumber())
            .birthyear(kakaoUser.getBirthYear())
            .gender(kakaoUser.getGender())
            .build();
        return memberRepository.save(newMember);
    }

    private ApiResponse<?> setResponseBody(Member member) {
        ApiResponse<?> apiResponse = null;

        if (member.getRole() == Role.PHOTOGRAPHER) {
            apiResponse = ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("photographer login")
                .data(profileService.getUniqueUrl(member.getId()))
                .build();
        } else if (member.getRole() == Role.PENDING) {
            apiResponse = ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("photographer join")
                .build();
        } else if (member.getRole() == Role.CUSTOMER) {
            apiResponse = ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("customer login")
                .build();
        }
        return apiResponse;
    }
}
