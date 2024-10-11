package com.foru.freebe.message.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.message.dto.DataResponse;
import com.foru.freebe.message.dto.MessageSendRequest;
import com.foru.freebe.message.dto.MessageSendResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageSendService {
    private final WebClient kakaoMessageWebClient;
    private static final String JOIN_TEMPLATE = "freebe_join";
    private static final String CUSTOMER_CANCEL_TEMPLATE = "customer_cancel";

    @Value("${kakao.alimtalk.user-id}")
    private String userId;

    public void sendWelcomeMessage(KakaoUser kakaoUser, boolean isNewMember) {
        if (!isNewMember) {
            return;
        }

        MessageSendRequest messageSendRequest = new MessageSendRequest(JOIN_TEMPLATE);

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

    public void sendCancellationNoticeToCustomer(String phoneNumber, String productName) {
        MessageSendRequest messageSendRequest = new MessageSendRequest(CUSTOMER_CANCEL_TEMPLATE);

        List<MessageSendResponse> response = kakaoMessageWebClient.post()
            .uri("/v2/sender/send")
            .header("userid", userId)
            .bodyValue(messageSendRequest.createCustomerCancelMessage(phoneNumber, productName))
            .retrieve()
            .bodyToFlux(MessageSendResponse.class)
            .collectList()
            .block();
    }
}