package com.foru.freebe.notice.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foru.freebe.errors.errorcode.MemberErrorCode;
import com.foru.freebe.errors.errorcode.NoticeErrorCode;
import com.foru.freebe.errors.errorcode.ProfileErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.notice.dto.NoticeDto;
import com.foru.freebe.notice.entity.Notice;
import com.foru.freebe.notice.repository.NoticeRepository;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotographerNoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public void updateNotice(Long photographerId, List<NoticeDto> requestList) {
        Member photographer = getMember(photographerId);
        Profile profile = getProfile(photographer);

        validateDuplicateTitles(requestList);
        noticeRepository.deleteAll();

        List<Notice> notices = requestList.stream()
            .map(request -> Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .profile(profile)
                .build())
            .collect(Collectors.toList());

        noticeRepository.saveAll(notices);
    }

    public List<NoticeDto> getNotices(Long photographerId) {
        Member photographer = getMember(photographerId);
        Profile profile = getProfile(photographer);

        List<Notice> noticeList = noticeRepository.findByProfile(profile);

        return noticeList.stream()
            .map(notice -> NoticeDto.builder()
                .title(notice.getTitle())
                .content(notice.getContent())
                .build())
            .collect(Collectors.toList());
    }

    private Profile getProfile(Member photographer) {
        return profileRepository.findByMember(photographer)
            .orElseThrow(() -> new RestApiException(ProfileErrorCode.MEMBER_NOT_FOUND));
    }

    private Member getMember(Long photographerId) {
        return memberRepository.findById(photographerId)
            .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateDuplicateTitles(List<NoticeDto> requestList) {
        Set<String> titleSet = new HashSet<>();

        requestList.forEach(request -> {
            if (!titleSet.add(request.getTitle())) {
                throw new RestApiException(NoticeErrorCode.TITLE_DUPLICATE);
            }
        });
    }
}
