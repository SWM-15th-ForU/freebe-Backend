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

    public final String CANCELLATION_AND_REFUND_POLICY = "취소 및 환불 규정";
    public final String RESCHEDULE_POLICY = "예약 변경 규정";

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public void updateNotice(Long photographerId, List<NoticeDto> requestList) {
        Member photographer = getMember(photographerId);
        Profile profile = getProfile(photographer);

        validateDuplicateTitles(requestList);
        validateIncludingEssentialTitle(requestList);
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

    private void validateIncludingEssentialTitle(List<NoticeDto> requestList) {
        boolean containsCancellationTitle = requestList.stream()
            .anyMatch(notice -> CANCELLATION_AND_REFUND_POLICY.equals(notice.getTitle()));

        boolean containsChangeTitle = requestList.stream()
            .anyMatch(notice -> RESCHEDULE_POLICY.equals(notice.getTitle()));

        if (!containsCancellationTitle || !containsChangeTitle) {
            throw new RestApiException(NoticeErrorCode.NOT_FOUND_ESSENTIAL_TITLE);
        }
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

    @Transactional
    public void deleteAllNotices(Member photographer) {
        Profile profile = getProfile(photographer);

        List<Notice> noticeList = noticeRepository.findByProfile(profile);
        noticeRepository.deleteAll(noticeList);
    }

    private Profile getProfile(Member photographer) {
        return profileRepository.findByMember(photographer)
            .orElseThrow(() -> new RestApiException(ProfileErrorCode.PROFILE_NOT_FOUND));
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
