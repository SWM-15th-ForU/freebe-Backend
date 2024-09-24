package com.foru.freebe.reservation.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.errorcode.ReservationErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.product.respository.ProductRepository;
import com.foru.freebe.reservation.dto.ReservationStatusUpdateRequest;
import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.entity.ReservationHistory;
import com.foru.freebe.reservation.entity.ReservationStatus;
import com.foru.freebe.reservation.repository.ReservationFormRepository;
import com.foru.freebe.reservation.repository.ReservationHistoryRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    ReservationFormRepository reservationFormRepository;

    @Mock
    ReservationHistoryRepository reservationHistoryRepository;

    private ReservationService reservationService;

    private ReservationForm mockReservationForm;

    @BeforeEach
    void setUp() {
        ReservationVerifier reservationVerifier = spy(new ReservationVerifier(productRepository, memberRepository));
        reservationService = new ReservationService(reservationVerifier, reservationFormRepository,
            reservationHistoryRepository);
    }

    @Nested
    @DisplayName("신청서 상태 변경 테스트")
    class updateReservationStatusTest {
        private void prepareMockReservationForm(Long memberId, Long formId, ReservationStatus currentStatus,
            Boolean isPhotographer) {
            mockReservationForm = mock(ReservationForm.class);
            when(mockReservationForm.getReservationStatus()).thenReturn(currentStatus);

            if (isPhotographer) {
                when(reservationFormRepository.findByPhotographerIdAndId(memberId, formId))
                    .thenReturn(Optional.of(mockReservationForm));
            } else {
                when(reservationFormRepository.findByCustomerIdAndId(memberId, formId))
                    .thenReturn(Optional.of(mockReservationForm));
            }
        }

        @Test
        @DisplayName("(성공) 고객이 신청서 접수 단계에서 예약을 취소한다")
        void successfulUpdateReservationStatus() {
            // given
            Long memberId = 1L;
            Long formId = 1L;
            Boolean isPhotographer = false;
            ReservationStatusUpdateRequest request = ReservationStatusUpdateRequest.builder()
                .updateStatus(ReservationStatus.CANCELLED_BY_CUSTOMER)
                .cancellationReason("개인 사정으로 인해 예약 취소합니다.")
                .build();

            prepareMockReservationForm(memberId, formId, ReservationStatus.NEW, isPhotographer);

            // when
            reservationService.updateReservationStatus(memberId, formId, request, isPhotographer);

            // then
            verify(mockReservationForm).updateReservationStatus(ReservationStatus.CANCELLED_BY_CUSTOMER);
            verify(reservationFormRepository).save(any(ReservationForm.class));
            verify(reservationHistoryRepository).save(any(ReservationHistory.class));
        }

        @Test
        @DisplayName("(실패) 고객이 상담중 단계에서 예약을 취소한다")
        void unsuccessfulUpdateReservationStatus() {
            // given
            Long memberId = 1L;
            Long formId = 1L;
            Boolean isPhotographer = false;
            ReservationStatusUpdateRequest request = ReservationStatusUpdateRequest.builder()
                .updateStatus(ReservationStatus.CANCELLED_BY_CUSTOMER)
                .cancellationReason("개인 사정으로 인해 예약 취소합니다.")
                .build();

            prepareMockReservationForm(memberId, formId, ReservationStatus.IN_PROGRESS, isPhotographer);

            // when, then
            RestApiException exception = assertThrows(RestApiException.class,
                () -> reservationService.updateReservationStatus(memberId, formId, request, isPhotographer));
            assertEquals(ReservationErrorCode.INVALID_RESERVATION_STATUS_FOR_CANCELLATION, exception.getErrorCode());
        }

        @DisplayName("(실패) 고객이 신청 사유를 입력하지 않고 예약을 취소한다")
        @Test
        void test() {
            // given
            Long memberId = 1L;
            Long formId = 1L;
            Boolean isPhotographer = false;
            ReservationStatusUpdateRequest request = ReservationStatusUpdateRequest.builder()
                .updateStatus(ReservationStatus.CANCELLED_BY_CUSTOMER)
                .build();

            prepareMockReservationForm(memberId, formId, ReservationStatus.NEW, isPhotographer);

            // when, then
            RestApiException exception = assertThrows(RestApiException.class,
                () -> reservationService.updateReservationStatus(memberId, formId, request, isPhotographer));
            assertEquals(CommonErrorCode.INVALID_PARAMETER, exception.getErrorCode());
        }

    }
}
