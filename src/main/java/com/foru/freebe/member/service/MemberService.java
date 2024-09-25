package com.foru.freebe.member.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.auth.dto.LoginResponse;
import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.jwt.model.JwtTokenModel;
import com.foru.freebe.jwt.service.JwtService;
import com.foru.freebe.member.dto.PhotographerJoinRequest;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.MemberTermAgreement;
import com.foru.freebe.member.entity.Role;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.member.repository.MemberTermAgreementRepository;
import com.foru.freebe.profile.service.ProfileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final ProfileService profileService;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final MemberTermAgreementRepository memberTermAgreementRepository;

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

        // 빌더 시작
        LoginResponse.LoginResponseBuilder builder = LoginResponse.builder();

        // 값 설정
        builder = builder.token(token);
        builder = validateRoleType(builder, member);

        // 최종적으로 build 호출하여 객체 생성
        return builder.build();
    }

    @Transactional
    public String joinPhotographer(Member member, PhotographerJoinRequest request, MultipartFile profileImage) throws
        IOException {
        Member photographer = completePhotographerSignup(member);

        savePhotographerAgreements(photographer, request);
        profileService.initialProfileSetting(photographer, profileImage, request.getProfileName());

        return profileService.getUniqueUrl(member.getId());
    }

    private LoginResponse.LoginResponseBuilder validateRoleType(LoginResponse.LoginResponseBuilder builder,
        Member member) {
        if (member.getRole() == Role.PHOTOGRAPHER) {
            return builder.message("photographer login")
                .uniqueUrl(profileService.getUniqueUrl(member.getId()));
        } else if (member.getRole() == Role.PHOTOGRAPHER_PENDING) {
            return builder.message("photographer join")
                .uniqueUrl(null);
        } else if (member.getRole() == Role.CUSTOMER) {
            return builder.message("customer login")
                .uniqueUrl(null);
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

    private Member completePhotographerSignup(Member member) {
        member.assignRole(Role.PHOTOGRAPHER);
        return memberRepository.save(member);
    }

    private void savePhotographerAgreements(Member member, PhotographerJoinRequest request) {
        MemberTermAgreement memberTermAgreement = MemberTermAgreement.builder()
            .member(member)
            .termsOfServiceAgreement(request.getTermsOfServiceAgreement())
            .privacyPolicyAgreement(request.getPrivacyPolicyAgreement())
            .marketingAgreement(request.getMarketingAgreement())
            .build();
        memberTermAgreementRepository.save(memberTermAgreement);
    }
}
