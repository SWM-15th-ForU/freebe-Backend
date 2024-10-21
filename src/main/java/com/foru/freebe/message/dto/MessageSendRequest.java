package com.foru.freebe.message.dto;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.foru.freebe.reservation.dto.CustomerAlimTalkInfo;
import com.foru.freebe.reservation.dto.CustomerCancelInfo;

public class MessageSendRequest {

    private static final String MESSAGE_TYPE = "AT";
    private static final String WEB_LINK_BUTTON_TYPE = "WL";
    private final String templateId;
    private final String profileKey;

    public MessageSendRequest(String templateId, String profileKey) {
        this.templateId = templateId;
        this.profileKey = profileKey;
    }

    public List<Map<String, String>> createJoinMessage(String phoneNumber, String name) {
        Map<String, String> mapRequestBody = new HashMap<>();
        List<Map<String, String>> jsonArray = new ArrayList<>();

        mapRequestBody.put("message_type", MESSAGE_TYPE);
        mapRequestBody.put("phn", phoneNumber);
        mapRequestBody.put("profile", profileKey);
        mapRequestBody.put("msg", name + "님, 프리비에 가입해주셔서 감사합니다.\n"
            + "\n"
            + "프리비와 함께 즐거운 촬영하세요 :)\n"
            + "\n"
            + "서비스 이용 중 궁금한 점은 카카오톡 채널로 문의하시거나 고객센터(070-8098-6471)로 전화주시면 상세히 안내 받으실 수 있습니다. 감사합니다.");
        mapRequestBody.put("tmplId", templateId);

        jsonArray.add(mapRequestBody);
        return jsonArray;
    }

    public List<Map<String, String>> createCustomerCancelMessage(String phoneNumber, String productName) {
        Map<String, String> mapRequestBody = new HashMap<>();
        List<Map<String, String>> jsonArray = new ArrayList<>();

        mapRequestBody.put("message_type", MESSAGE_TYPE);
        mapRequestBody.put("phn", phoneNumber);
        mapRequestBody.put("profile", profileKey);

        String messageTemplate = """
            안녕하세요 고객님,
            고객님의 요청으로 #{PRODUCT_NAME} 촬영 예약이 취소되었습니다.

            언제든 다시 찾아주세요. 감사합니다!""";

        String formattedMessage = messageTemplate.replace("#{PRODUCT_NAME}", productName);
        mapRequestBody.put("msg", formattedMessage);
        mapRequestBody.put("tmplId", templateId);

        jsonArray.add(mapRequestBody);
        return jsonArray;
    }

    public List<Map<String, Object>> createPhotographerCancelledMessage(CustomerCancelInfo cancelledInfo) {
        Map<String, Object> mapRequestBody = new HashMap<>();
        List<Map<String, Object>> jsonArray = new ArrayList<>();

        mapRequestBody.put("message_type", MESSAGE_TYPE);
        mapRequestBody.put("phn", cancelledInfo.getPhotographerPhoneNumber());
        mapRequestBody.put("profile", profileKey);

        String messageTemplate = """
            고객의 요청으로 촬영 예약이 취소되었습니다.
            아래 [확인하기] 버튼을 눌러 상세 내역을 확인해주세요.

            ■ 고객명: {0}
            ■ 촬영상품: {1}
            ■ 취소사유: {2}""";

        String messageFormat = MessageFormat.format(
            messageTemplate,
            cancelledInfo.getCustomerName(),
            cancelledInfo.getProductTitle(),
            cancelledInfo.getCancellationReason()
        );

        mapRequestBody.put("msg", messageFormat);
        mapRequestBody.put("tmplId", templateId);

        String webUrl = "https://www.freebe.co.kr/photographer/reservation/" + cancelledInfo.getReservationId();
        Button button1 = Button.builder()
            .name("확인하기")
            .type(WEB_LINK_BUTTON_TYPE)
            .urlPc(webUrl)
            .urlMobile(webUrl)
            .build();

        mapRequestBody.put("button1", convertButtonToMap(button1));

        jsonArray.add(mapRequestBody);
        return jsonArray;
    }

    public List<Map<String, Object>> createCustomerCancelledMessage(CustomerAlimTalkInfo cancelledInfo) {
        Map<String, Object> mapRequestBody = new HashMap<>();
        List<Map<String, Object>> jsonArray = new ArrayList<>();

        mapRequestBody.put("message_type", MESSAGE_TYPE);
        mapRequestBody.put("phn", cancelledInfo.getCustomerPhoneNumber());
        mapRequestBody.put("profile", profileKey);

        String messageTemplate = """
            안녕하세요 고객님,
            {0} 촬영이 취소되어 안내드립니다.

            ■ 취소사유: {1}

            다시 뵐 날을 기다리겠습니다. 감사합니다.""";

        String messageFormat = MessageFormat.format(
            messageTemplate,
            cancelledInfo.getProductTitle(),
            cancelledInfo.getCancellationReason()
        );

        mapRequestBody.put("msg", messageFormat);
        mapRequestBody.put("tmplId", templateId);

        String webUrl = "https://www.freebe.co.kr/customer/reservation/" + cancelledInfo.getReservationId();
        Button button1 = Button.builder()
            .name("확인하기")
            .type(WEB_LINK_BUTTON_TYPE)
            .urlPc(webUrl)
            .urlMobile(webUrl)
            .build();

        mapRequestBody.put("button1", convertButtonToMap(button1));

        jsonArray.add(mapRequestBody);
        return jsonArray;
    }

    //ToDo: rebase 후 촬영장소, 공지사항 실제데이터로 변경.
    public Object createWaitShootingMessage(CustomerAlimTalkInfo customerAlimTalkInfo) {
        Map<String, Object> mapRequestBody = new HashMap<>();
        List<Map<String, Object>> jsonArray = new ArrayList<>();

        mapRequestBody.put("message_type", MESSAGE_TYPE);
        mapRequestBody.put("phn", customerAlimTalkInfo.getCustomerPhoneNumber());
        mapRequestBody.put("profile", profileKey);

        String messageTemplate = """
            [{0}] 촬영이 최종 확정되었습니다!

             ■ 촬영날짜: {1}
             ■ 촬영시간: {2}
             ■ 촬영장소: {3}

            [공지사항]
            {4}""";

        String messageFormat = MessageFormat.format(
            messageTemplate,
            customerAlimTalkInfo.getProductTitle(),
            customerAlimTalkInfo.getShootingDate().getDate(),
            customerAlimTalkInfo.getShootingDate().getStartTime().toString() + " ~ "
                + customerAlimTalkInfo.getShootingDate().getEndTime().toString(),
            "임의장소",
            "공지사항"
        );

        mapRequestBody.put("msg", messageFormat);
        mapRequestBody.put("tmplId", templateId);

        String webUrl = "https://www.freebe.co.kr/customer/reservation/" + customerAlimTalkInfo.getReservationId();
        Button button1 = Button.builder()
            .name("자세히보기")
            .type(WEB_LINK_BUTTON_TYPE)
            .urlPc(webUrl)
            .urlMobile(webUrl)
            .build();

        String noticeUrl = "https://www.freebe.co.kr/" + customerAlimTalkInfo.getProfileName() + "/notice";
        Button button2 = Button.builder()
            .name("공지사항 확인하기")
            .type(WEB_LINK_BUTTON_TYPE)
            .urlPc(noticeUrl)
            .urlMobile(noticeUrl)
            .build();

        mapRequestBody.put("button1", convertButtonToMap(button1));
        mapRequestBody.put("button2", convertButtonToMap(button2));

        jsonArray.add(mapRequestBody);
        return jsonArray;
    }

    private Map<String, String> convertButtonToMap(Button button) {
        return Map.of(
            "name", button.getName(),
            "type", button.getType(),
            "url_pc", button.getUrlPc(),
            "url_mobile", button.getUrlMobile()
        );
    }
}
