package com.foru.freebe.profile.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    private static final String BASE_URL = "https://freebe.co.kr/photographer/";

    public String registerUniqueUrl(Long id) {
        String uniqueUrl = null;
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Member not found"));

        Profile profile = member.getProfile();

        if (profile == null || profile.getUniqueUrl() == null) {
            uniqueUrl = generateUniqueUrl();

            if (profile == null) {
                profile = Profile.builder()
                    .uniqueUrl(uniqueUrl)
                    .introductionContent(null)
                    .bannerImageUrl(null)
                    .build();
                member.updateProfile(profile);
            } else {
                profile.updateUniqueUrl(uniqueUrl);
            }
        }
        profileRepository.save(profile);

        return uniqueUrl;
    }

    private String generateUniqueUrl() {
        String uniqueId = UUID.randomUUID().toString();
        return BASE_URL + uniqueId;
    }
}
