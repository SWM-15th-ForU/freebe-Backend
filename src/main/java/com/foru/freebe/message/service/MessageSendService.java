package com.foru.freebe.message.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.message.dto.DataResponse;
import com.foru.freebe.message.dto.MessageSendRequest;
import com.foru.freebe.message.dto.MessageSendResponse;
import com.foru.freebe.reservation.dto.alimtalk.CustomerCancelInfo;
import com.foru.freebe.reservation.dto.alimtalk.StatusUpdateNotice;
import com.foru.freebe.reservation.entity.ReservationStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageSendService {
    private final WebClient kakaoMessageWebClient;
    private static final String JOIN_TEMPLATE = "freebe_join";
    private static final String CUSTOMER_NEW_TEMPLATE = "c_new_1014";
    private static final String CUSTOMER_CANCEL_TEMPLATE = "customer_cancel";
    private static final String PHOTOGRAPHER_CANCELLED_TEMPLATE = "photographer_cancelled";
    private static final String CUSTOMER_CANCELLED_TEMPLATE = "customer_cancelled";
    private static final String CUSTOMER_WAIT_SHOOTING = "customer_wait_shooting";

    @Value("${kakao.alimtalk.user-id}")
    private String userId;

    @Value("${kakao.alimtalk.profile_key}")
    private String profileKey;

    public void sendWelcomeMessage(KakaoUser kakaoUser, boolean isNewMember) {
        if (!isNewMember) {
            return;
        }

        MessageSendRequest messageSendRequest = new MessageSendRequest(JOIN_TEMPLATE, profileKey);

        List<MessageSendResponse> response = kakaoMessageWebClient.post()
            .uri("/v2/sender/send")
            .header("userid", userId)
            .bodyValue(messageSendRequest.createJoinMessage(kakaoUser.getPhoneNumber(), kakaoUser.getUserName()))
            .retrieve()
            .bodyToFlux(MessageSendResponse.class)
            .collectList()
            .block();

        MessageSendResponse messageSendResponse = response.get(0);
        DataResponse dataResponse = messageSendResponse.getData();
    }

    public void sendReservationCompleteMessageToCustomer(String name, String phoneNumber, String productTitle,
        Long formId) {
        MessageSendRequest messageSendRequest = new MessageSendRequest(CUSTOMER_NEW_TEMPLATE, profileKey);

        List<MessageSendResponse> response = kakaoMessageWebClient.post()
            .uri("/v2/sender/send")
            .header("userid", userId)
            .bodyValue(messageSendRequest.createCustomerNewMessage(name, phoneNumber, productTitle, formId))
            .retrieve()
            .bodyToFlux(MessageSendResponse.class)
            .collectList()
            .block();
    }

    public void sendCancellationNoticeToCustomer(String phoneNumber, String productName) {
        MessageSendRequest messageSendRequest = new MessageSendRequest(CUSTOMER_CANCEL_TEMPLATE, profileKey);

        List<MessageSendResponse> response = kakaoMessageWebClient.post()
            .uri("/v2/sender/send")
            .header("userid", userId)
            .bodyValue(messageSendRequest.createCustomerCancelMessage(phoneNumber, productName))
            .retrieve()
            .bodyToFlux(MessageSendResponse.class)
            .collectList()
            .block();
    }

    public void sendCancellationNoticeToPhotographer(CustomerCancelInfo customerCancelInfo) {
        MessageSendRequest messageSendRequest = new MessageSendRequest(PHOTOGRAPHER_CANCELLED_TEMPLATE, profileKey);

        List<MessageSendResponse> response = kakaoMessageWebClient.post()
            .uri("/v2/sender/send")
            .header("userid", userId)
            .bodyValue(messageSendRequest.createPhotographerCancelledMessage(customerCancelInfo))
            .retrieve()
            .bodyToFlux(MessageSendResponse.class)
            .collectList()
            .block();
    }

    public void sendStatusUpdateNoticeToCustomer(StatusUpdateNotice statusUpdateNotice) {
        if (statusUpdateNotice.getUpdatedStatus() == ReservationStatus.CANCELLED_BY_PHOTOGRAPHER) {
            sendCancelledNoticeToCustomer(statusUpdateNotice);
        } else if (statusUpdateNotice.getUpdatedStatus() == ReservationStatus.WAITING_FOR_PHOTO) {
            sendWaitShootingNoticeToCustomer(statusUpdateNotice);
        }
    }

    private void sendCancelledNoticeToCustomer(StatusUpdateNotice statusUpdateNotice) {
        MessageSendRequest messageSendRequest = new MessageSendRequest(CUSTOMER_CANCELLED_TEMPLATE, profileKey);

        List<MessageSendResponse> response = kakaoMessageWebClient.post()
            .uri("/v2/sender/send")
            .header("userid", userId)
            .bodyValue(messageSendRequest.createCustomerCancelledMessage(statusUpdateNotice))
            .retrieve()
            .bodyToFlux(MessageSendResponse.class)
            .collectList()
            .block();
    }

    private void sendWaitShootingNoticeToCustomer(StatusUpdateNotice statusUpdateNotice) {
        MessageSendRequest messageSendRequest = new MessageSendRequest(CUSTOMER_WAIT_SHOOTING, profileKey);

        List<MessageSendResponse> response = kakaoMessageWebClient.post()
            .uri("/v2/sender/send")
            .header("userid", userId)
            .bodyValue(messageSendRequest.createWaitShootingMessage(statusUpdateNotice))
            .retrieve()
            .bodyToFlux(MessageSendResponse.class)
            .collectList()
            .block();

        MessageSendResponse messageSendResponse = response.get(0);
    }
}