package com.foru.freebe.notice.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foru.freebe.errors.errorcode.MemberErrorCode;
import com.foru.freebe.errors.errorcode.NoticeErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.notice.dto.NoticeRequest;
import com.foru.freebe.notice.entity.Notice;
import com.foru.freebe.notice.repository.NoticeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void updateNotice(Long photographerId, List<NoticeRequest> requestList) {
        Member photographer = memberRepository.findById(photographerId)
            .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));

        validateDuplicateTitles(requestList);
        noticeRepository.deleteAll();

        List<Notice> notices = requestList.stream()
            .map(request -> Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(photographer)
                .build())
            .collect(Collectors.toList());

        noticeRepository.saveAll(notices);
    }

    private void validateDuplicateTitles(List<NoticeRequest> requestList) {
        Set<String> titleSet = new HashSet<>();

        requestList.forEach(request -> {
            if (!titleSet.add(request.getTitle())) {
                throw new RestApiException(NoticeErrorCode.TITLE_DUPLICATE);
            }
        });
    }
}
