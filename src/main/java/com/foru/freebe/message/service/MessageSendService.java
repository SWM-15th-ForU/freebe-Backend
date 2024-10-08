package com.foru.freebe.message.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.message.dto.DataResponse;
import com.foru.freebe.message.dto.MessageSendResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageSendService {
    private final WebClient kakaoMessageWebClient;
    private static final String TEMPLATE_ID = "freebe_join";
    private static final String MESSAGE_TYPE = "AT";
    @Value("${kakao.alimtalk.user-id}")
    private String userId;
    @Value("${kakao.alimtalk.profile_key}")
    private String profileKey;

    public void messageSendRequest(KakaoUser kakaoUser, boolean isNewMember) {
        if (!isNewMember) {
            return;
        }
        List<MessageSendResponse> response = kakaoMessageWebClient.post()
            .uri("/v2/sender/send")
            .header("userid", userId)
            .bodyValue(setBodyValue(kakaoUser.getPhoneNumber(), kakaoUser.getUserName()))
            .retrieve()
            .bodyToFlux(MessageSendResponse.class)
            .collectList()
            .block();

        MessageSendResponse messageSendResponse = response.get(0);
        DataResponse dataResponse = messageSendResponse.getData();
    }

    private List<Map<String, String>> setBodyValue(String phoneNumber, String name) {
        Map<String, String> mapRequestBody = new HashMap<String, String>();
        List<Map<String, String>> jsonArray = new ArrayList<Map<String, String>>();

        mapRequestBody.put("message_type", MESSAGE_TYPE);
        mapRequestBody.put("phn", phoneNumber);
        mapRequestBody.put("profile", profileKey);
        mapRequestBody.put("msg", name + "님, 프리비에 가입해주셔서 감사합니다.\n"
            + "\n"
            + "프리비와 함께 즐거운 촬영하세요 :)\n"
            + "\n"
            + "서비스 이용 중 궁금한 점은 카카오톡 채널로 문의하시거나 고객센터(070-8098-6471)로 전화주시면 상세히 안내 받으실 수 있습니다. 감사합니다.");
        mapRequestBody.put("tmplId", TEMPLATE_ID);

        jsonArray.add(mapRequestBody);
        return jsonArray;
    }
}
