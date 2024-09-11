package com.foru.freebe.member.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.common.service.S3ImageService;
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
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.entity.ProfileImage;
import com.foru.freebe.profile.repository.ProfileImageRepository;
import com.foru.freebe.profile.repository.ProfileRepository;
import com.foru.freebe.profile.service.ProfileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final ProfileService profileService;
    private final JwtService jwtService;
    private final S3ImageService s3ImageService;
    private final MemberRepository memberRepository;
    private final MemberTermAgreementRepository memberTermAgreementRepository;
    private static final int PROFILE_THUMBNAIL_SIZE = 100;
    private final ProfileRepository profileRepository;
    private final ProfileImageRepository profileImageRepository;

    @Value("${AWS_S3_PROFILE_IMAGE_PATH}")
    private String AWS_S3_PROFILE_IMAGE_PATH;

    @Transactional
    public ResponseEntity<ApiResponse<?>> findOrRegisterMember(KakaoUser kakaoUser, Role role) {
        Member member = memberRepository.findByKakaoId(kakaoUser.getKakaoId())
            .orElseGet(() -> {
                if (role == Role.PHOTOGRAPHER) {
                    return registerNewMember(kakaoUser, Role.PHOTOGRAPHER_PENDING);
                }
                return registerNewMember(kakaoUser, role);
            });

        ApiResponse<?> body = setResponseBody(member);

        JwtTokenModel token = jwtService.generateToken(member.getId());
        HttpHeaders headers = jwtService.setTokenHeaders(token);

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    @Transactional
    public ApiResponse<String> joinPhotographer(Member member, PhotographerJoinRequest request,
        MultipartFile profileImage) throws IOException {
        Member photographer = completePhotographerSignup(member, request.getInstagramId());
        savePhotographerAgreements(photographer, request);

        String url = profileService.getUniqueUrl(member.getId());
        saveProfileImage(photographer, profileImage);
        return ApiResponse.<String>builder()
            .status(HttpStatus.OK.value())
            .data(url)
            .message("Successfully joined")
            .build();
    }

    private void saveProfileImage(Member photographer, MultipartFile profileImage) throws IOException {
        List<MultipartFile> profileImages = Collections.singletonList(profileImage);

        List<String> originalImageUrls = s3ImageService.uploadOriginalImages(profileImages, AWS_S3_PROFILE_IMAGE_PATH);
        List<String> thumbnailImageUrls = s3ImageService.uploadThumbnailImages(profileImages, AWS_S3_PROFILE_IMAGE_PATH,
            PROFILE_THUMBNAIL_SIZE);

        String originalImageUrl = originalImageUrls.get(0);
        String thumbnailImageUrl = thumbnailImageUrls.get(0);

        Profile profile = profileRepository.findByMember(photographer)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        ProfileImage image = ProfileImage.builder()
            .profile(profile)
            .originUrl(originalImageUrl)
            .thumbnailUrl(thumbnailImageUrl)
            .build();
        profileImageRepository.save(image);
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
        } else if (member.getRole() == Role.PHOTOGRAPHER_PENDING) {
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

    private Member completePhotographerSignup(Member member, String instagramId) {
        member.assignRole(Role.PHOTOGRAPHER);
        member.assignInstagramId(instagramId);
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
