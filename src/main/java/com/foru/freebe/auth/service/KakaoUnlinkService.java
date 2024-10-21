package com.foru.freebe.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import com.foru.freebe.auth.dto.UnlinkRequest;
import com.foru.freebe.errors.errorcode.MemberErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.DeletedMember;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.Role;
import com.foru.freebe.member.repository.DeletedMemberRepository;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.notice.service.PhotographerNoticeService;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.respository.ProductRepository;
import com.foru.freebe.product.service.PhotographerProductService;
import com.foru.freebe.profile.service.PhotographerProfileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoUnlinkService {
    final String AUTHORIZATION_HEADER = "Authorization";
    final String KAKAO_AUTH_PREFIX = "KakaoAK ";
    final String kakaoUnlinkUrl = "https://kapi.kakao.com/v1/user/unlink";

    private final WebClient webClient;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final PhotographerProductService photographerProductService;
    private final PhotographerProfileService photographerProfileService;
    private final DeletedMemberRepository deletedMemberRepository;
    private final PhotographerNoticeService photographerNoticeService;

    @Value("${KAKAO_API_ADMIN_KEY}")
    private String adminKey;

    @Transactional
    public void unlinkKakaoAccount(Long memberId, UnlinkRequest unlinkRequest) {
        Member member = getMember(memberId);
        Long kakaoUserId = member.getKakaoId();

        try {
            deleteInfoOfPhotographer(member);

            ResponseEntity<String> response = unlinkKakao(kakaoUserId);
            createDeletedMember(member.getId(), member, unlinkRequest.getReason());

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RestApiException(MemberErrorCode.ERROR_MEMBER_LEAVING_FAILED);
            }
        } catch (WebClientException e) {
            throw new RestApiException(MemberErrorCode.ERROR_MEMBER_LEAVING_FAILED);
        }
    }

    private void deleteInfoOfPhotographer(Member member) {
        if (member.getRole() == Role.PHOTOGRAPHER) {
            deletePhotographerProducts(member);
            photographerNoticeService.deleteAllNotices(member);
            photographerProfileService.deleteProfile(member);
        }
    }

    private ResponseEntity<String> unlinkKakao(Long kakaoUserId) {
        return webClient.post()
            .uri(kakaoUnlinkUrl)
            .header(AUTHORIZATION_HEADER, KAKAO_AUTH_PREFIX + adminKey)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(constructUnlinkParams(kakaoUserId))
            .retrieve()
            .toEntity(String.class)
            .block();
    }

    private static MultiValueMap<String, Object> constructUnlinkParams(Long kakaoUserId) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", kakaoUserId.toString());
        return params;
    }

    private void createDeletedMember(Long memberId, Member member, String reason) {
        member.updateMemberRoleToLeavingStatus();

        DeletedMember deletedMember = DeletedMember.builder()
            .kakaoId(member.getKakaoId())
            .memberId(memberId)
            .unlinkReason(reason)
            .build();

        deletedMemberRepository.save(deletedMember);
        member.deleteKakaoId();
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private void handleMemberLeaving(Member member, String reason) {
        if (member.getRole() == Role.PHOTOGRAPHER) {
            member.updateMemberRoleToLeavingStatus();
            deletePhotographerProducts(member);
            photographerNoticeService.deleteAllNotices(member);
            photographerProfileService.deleteProfile(member);
            createDeletedMember(member.getId(), member, reason);
        } else if (member.getRole() == Role.PHOTOGRAPHER_PENDING) {
            member.updateMemberRoleToLeavingStatus();
            createDeletedMember(member.getId(), member, reason);
        } else if (member.getRole() == Role.CUSTOMER) {
            member.updateMemberRoleToLeavingStatus();
            createDeletedMember(member.getId(), member, reason);
        }
    }

    private void deletePhotographerProducts(Member member) {
        List<Product> productList = productRepository.findByMember(member);
        for (Product product : productList) {
            photographerProductService.deleteProductForUnlike(product);
        }
    }

}
