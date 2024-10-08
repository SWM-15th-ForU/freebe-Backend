package com.foru.freebe.auth.service;

import org.springframework.stereotype.Service;

import com.foru.freebe.auth.dto.LoginResponse;
import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.errors.errorcode.MemberErrorCode;
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
        builder = validateRoleType(builder, member, role);

        return builder.build();
    }

    private LoginResponse.LoginResponseBuilder validateRoleType(LoginResponse.LoginResponseBuilder builder,
        Member member, Role requestedRole) {
        if (member.getRole() == requestedRole ||
            member.getRole() == Role.PHOTOGRAPHER_PENDING && requestedRole.equals(Role.PHOTOGRAPHER)) {
            return handleSameRoleLogin(builder, member);
        } else {
            return handleRoleChange(builder, member, requestedRole);
        }
    }

    private LoginResponse.LoginResponseBuilder handleSameRoleLogin(LoginResponse.LoginResponseBuilder builder,
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
        throw new RestApiException(MemberErrorCode.INVALID_LOGIN_REQUEST);
    }

    private LoginResponse.LoginResponseBuilder handleRoleChange(LoginResponse.LoginResponseBuilder builder,
        Member member, Role requestedRole) {
        if (member.getRole() == Role.PHOTOGRAPHER && requestedRole == Role.CUSTOMER) {
            return builder.message("customer login")
                .profileName(null);
        } else if (member.getRole() == Role.PHOTOGRAPHER_PENDING && requestedRole == Role.CUSTOMER) {
            member.assignRole(Role.CUSTOMER);
            return builder.message("customer login")
                .profileName(null);
        } else if (member.getRole() == Role.CUSTOMER && requestedRole == Role.PHOTOGRAPHER) {
            member.assignRole(Role.PHOTOGRAPHER_PENDING);
            return builder.message("photographer join")
                .profileName(null);
        }
        throw new RestApiException(MemberErrorCode.INVALID_LOGIN_REQUEST);

    private Member registerNewMember(KakaoUser kakaoUser, Role role) {
        Member newMember = Member.builder(kakaoUser.getKakaoId(), role, kakaoUser.getUserName(),
                kakaoUser.getEmail(), kakaoUser.getPhoneNumber())
            .birthYear(kakaoUser.getBirthYear())
            .birthDay(kakaoUser.getBirthDay())
            .gender(kakaoUser.getGender())
            .build();
        return memberRepository.save(newMember);
    }
}
