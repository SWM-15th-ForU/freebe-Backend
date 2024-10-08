package com.foru.freebe.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.foru.freebe.auth.dto.UnlinkRequest;
import com.foru.freebe.errors.errorcode.MemberErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.DeletedMember;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.Role;
import com.foru.freebe.member.repository.DeletedMemberRepository;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.respository.ProductRepository;
import com.foru.freebe.product.service.PhotographerProductService;
import com.foru.freebe.profile.service.ProfileService;

import jakarta.transaction.Transactional;

@Service
public class KakaoUnlinkService {
    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final PhotographerProductService photographerProductService;
    private final ProfileService profileService;
    private final DeletedMemberRepository deletedMemberRepository;

    public KakaoUnlinkService(RestTemplateBuilder restTemplateBuilder, MemberRepository memberRepository,
        ProductRepository productRepository, PhotographerProductService photographerProductService,
        ProfileService profileService, DeletedMemberRepository deletedMemberRepository) {
        restTemplate = restTemplateBuilder.build();
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.photographerProductService = photographerProductService;
        this.profileService = profileService;
        this.deletedMemberRepository = deletedMemberRepository;
    }

    @Value("${KAKAO_API_ADMIN_KEY}")
    private String adminKey;

    @Transactional
    public void unlinkKakaoAccount(Long memberId, UnlinkRequest unlinkRequest) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));

        Long kakaoUserId = member.getKakaoId(); // 회원의 카카오 유저 ID

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + adminKey);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", kakaoUserId);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);

        try {
            String kakaoUnlinkUrl = "https://kapi.kakao.com/v1/user/unlink";
            ResponseEntity<String> response = restTemplate.postForEntity(kakaoUnlinkUrl, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                handleMemberLeaving(member, unlinkRequest.getReason());
            }
        } catch (RestClientException e) {
            throw new RestApiException(MemberErrorCode.ERROR_MEMBER_LEAVING_FAILED);
        }
    }

    private void handleMemberLeaving(Member member, String reason) {
        if (member.getRole() == Role.PHOTOGRAPHER) {
            member.updateMemberRole(Role.PHOTOGRAPHER_LEAVING);
            createDeletedMember(member.getId(), member, reason);
            deletePhotographerProducts(member.getId(), member);
            profileService.deleteProfile(member);
        } else if (member.getRole() == Role.CUSTOMER) {
            member.updateMemberRole(Role.CUSTOMER_LEAVING);
            createDeletedMember(member.getId(), member, reason);
        }
    }

    private void deletePhotographerProducts(Long memberId, Member member) {
        List<Product> productList = productRepository.findByMember(member);
        for (Product product : productList) {
            photographerProductService.deleteProduct(product.getId(), memberId);
        }
    }

    private void createDeletedMember(Long memberId, Member member, String reason) {
        DeletedMember deletedMember = DeletedMember.builder()
            .kakaoId(member.getKakaoId())
            .memberId(memberId)
            .unlinkReason(reason)
            .build();
        deletedMemberRepository.save(deletedMember);
        member.deleteKakaoId();
    }
}
