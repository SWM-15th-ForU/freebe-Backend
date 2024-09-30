package com.foru.freebe.auth.service;

import org.springframework.stereotype.Service;

import com.foru.freebe.auth.dto.LoginResponse;
import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.jwt.model.JwtTokenModel;
import com.foru.freebe.jwt.service.JwtService;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.Role;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.profile.service.ProfileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {
    private final JwtService jwtService;
    private final ProfileService profileService;
    private final MemberRepository memberRepository;

    @Transactional
    public LoginResponse findOrRegisterMember(KakaoUser kakaoUser, Role role) {
        Member member = memberRepository.findByKakaoId(kakaoUser.getKakaoId())
            .orElseGet(() -> {
                if (role == Role.PHOTOGRAPHER) {
                    return registerNewMember(kakaoUser, Role.PHOTOGRAPHER_PENDING);
                }
                return registerNewMember(kakaoUser, role);
            });

        JwtTokenModel token = jwtService.generateToken(member.getId());

        LoginResponse.LoginResponseBuilder builder = LoginResponse.builder();
        builder = builder.token(token);
        builder = validateRoleType(builder, member);

        return builder.build();
    }

    private LoginResponse.LoginResponseBuilder validateRoleType(LoginResponse.LoginResponseBuilder builder,
        Member member) {
        if (member.getRole() == Role.PHOTOGRAPHER) {
            return builder.message("photographer login")
                .profileName(profileService.getProfileName(member.getId()));
        } else if (member.getRole() == Role.PHOTOGRAPHER_PENDING) {
            return builder.message("photographer join")
                .profileName(null);
        } else if (member.getRole() == Role.CUSTOMER) {
            return builder.message("customer login")
                .profileName(null);
        }
        throw new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
    }

    private Member registerNewMember(KakaoUser kakaoUser, Role role) {
        Member newMember = Member.builder(kakaoUser.getKakaoId(), role, kakaoUser.getUserName(),
                kakaoUser.getEmail(), kakaoUser.getPhoneNumber())
            .birthyear(kakaoUser.getBirthYear())
            .gender(kakaoUser.getGender())
            .build();
        return memberRepository.save(newMember);
    }
}
