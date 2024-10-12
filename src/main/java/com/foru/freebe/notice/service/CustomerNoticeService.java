package com.foru.freebe.notice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.ProfileErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.notice.dto.NoticeDto;
import com.foru.freebe.notice.entity.Notice;
import com.foru.freebe.notice.repository.NoticeRepository;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerNoticeService {

    private final ProfileRepository profileRepository;
    private final NoticeRepository noticeRepository;

    public List<NoticeDto> getNotices(String profileName) {
        Profile profile = getProfile(profileName);

        List<Notice> noticeList = noticeRepository.findByProfile(profile);

        return noticeList.stream()
            .map(notice -> NoticeDto.builder()
                .title(notice.getTitle())
                .content(notice.getContent())
                .build())
            .collect(Collectors.toList());
    }

    private Profile getProfile(String profileName) {
        Profile profile = profileRepository.findByProfileName(profileName)
            .orElseThrow(() -> new RestApiException(ProfileErrorCode.PROFILE_NAME_NOT_FOUND));
        return profile;
    }
}
