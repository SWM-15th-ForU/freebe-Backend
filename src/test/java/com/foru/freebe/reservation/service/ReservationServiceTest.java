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
import com.foru.freebe.product.respository.ProductRepository;
import com.foru.freebe.profile.repository.ProfileRepository;
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
    private ProfileRepository profileRepository;

    @Mock
    ReservationFormRepository reservationFormRepository;

    @Mock
    ReservationHistoryRepository reservationHistoryRepository;

    private ReservationVerifier reservationVerifier;

    private ReservationService reservationService;

    private ReservationForm mockReservationForm;

    @BeforeEach
    void setUp() {
        reservationVerifier = spy(new ReservationVerifier(productRepository, profileRepository));
        reservationService = new ReservationService(reservationVerifier, reservationFormRepository,
            reservationHistoryRepository);
    }

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

    @Nested
    @DisplayName("고객측 신청서 취소 테스트")
    class CustomerReservationCancellationTest {
        Long memberId = 1L;
        Long formId = 1L;
        Boolean isPhotographer = false;

        @Test
        @DisplayName("(성공) 고객이 '새 신청' 단계에서 예약을 취소한다")
        void successfullyCancelsReservation() {
            // given
            ReservationStatusUpdateRequest request = new ReservationStatusUpdateRequest(
                ReservationStatus.CANCELLED_BY_CUSTOMER, "개인 사정으로 예약 취소합니다");

            prepareMockReservationForm(memberId, formId, ReservationStatus.NEW, isPhotographer);

            // when
            reservationService.updateReservationStatus(memberId, formId, request, isPhotographer);

            // then
            verify(mockReservationForm).changeReservationStatus(ReservationStatus.CANCELLED_BY_CUSTOMER);
            verify(reservationFormRepository).save(any(ReservationForm.class));
            verify(reservationHistoryRepository).save(any(ReservationHistory.class));
        }

        @Test
        @DisplayName("(실패) 고객이 '상담중' 단계에서 예약을 취소한다")
        void failsToCancelReservation() {
            // given
            ReservationStatusUpdateRequest request = new ReservationStatusUpdateRequest(
                ReservationStatus.CANCELLED_BY_CUSTOMER, "개인 사정으로 예약 취소합니다");
            prepareMockReservationForm(memberId, formId, ReservationStatus.IN_PROGRESS, isPhotographer);

            // when, then
            RestApiException exception = assertThrows(RestApiException.class,
                () -> reservationService.updateReservationStatus(memberId, formId, request, isPhotographer));
            assertEquals(ReservationErrorCode.INVALID_STATUS_TRANSITION, exception.getErrorCode());
        }

        @Test
        @DisplayName("(실패) 고객이 취소 사유를 입력하지 않고 예약을 취소한다")
        void failsToCancelReservationWithoutReason() {
            // given
            ReservationStatusUpdateRequest request = new ReservationStatusUpdateRequest(
                ReservationStatus.CANCELLED_BY_CUSTOMER, null);

            prepareMockReservationForm(memberId, formId, ReservationStatus.NEW, isPhotographer);

            // when, then
            RestApiException exception = assertThrows(RestApiException.class,
                () -> reservationService.updateReservationStatus(memberId, formId, request, isPhotographer));
            assertEquals(CommonErrorCode.INVALID_PARAMETER, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("사진작가측 신청서 상태 변경 테스트")
    class ReservationFormStatusChangeTest {
        Long memberId = 1L;
        Long formId = 1L;
        Boolean isPhotographer = true;

        @Test
        @DisplayName("(성공) '새 신청' 상태의 신청서를 수락해 '상담중' 상태로 변경한다")
        void newToInProgress() {
            // given
            ReservationStatusUpdateRequest request = new ReservationStatusUpdateRequest(
                ReservationStatus.IN_PROGRESS, null);

            prepareMockReservationForm(memberId, formId, ReservationStatus.NEW, isPhotographer);

            // when
            reservationService.updateReservationStatus(memberId, formId, request, isPhotographer);

            // then
            verify(reservationVerifier).validateStatusChange(ReservationStatus.NEW, request, isPhotographer);
            verify(mockReservationForm).changeReservationStatus(ReservationStatus.IN_PROGRESS);
            verify(reservationFormRepository).save(mockReservationForm);
            verify(reservationHistoryRepository).save(any(ReservationHistory.class));
        }

        @Test
        @DisplayName("(성공) 촬영일정이 확정되면 '상담중' 상태의 신청서를 '입금대기' 상태로 변경한다")
        void inProgressToWaitingForDeposit() {
            //ToDo: 확정된 촬영 일정 등록하는 API 개발 필요
        }

        @Test
        @DisplayName("(성공) '입금대기' 상태에서 '취소' 상태로 변경한다")
        void waitingForDepositToCancelledByPhotographer() {
            // given
            ReservationStatusUpdateRequest request = new ReservationStatusUpdateRequest(
                ReservationStatus.CANCELLED_BY_PHOTOGRAPHER, "고객 요청으로 취소함");

            prepareMockReservationForm(memberId, formId, ReservationStatus.WAITING_FOR_DEPOSIT, isPhotographer);

            // when
            reservationService.updateReservationStatus(memberId, formId, request, isPhotographer);

            // then
            verify(reservationVerifier).validateStatusChange(ReservationStatus.WAITING_FOR_DEPOSIT, request,
                isPhotographer);
            assertDoesNotThrow(
                () -> reservationVerifier.validateStatusChange(ReservationStatus.WAITING_FOR_DEPOSIT, request,
                    isPhotographer));
            verify(mockReservationForm).changeReservationStatus(ReservationStatus.CANCELLED_BY_PHOTOGRAPHER);
            verify(reservationFormRepository).save(mockReservationForm);
            verify(reservationHistoryRepository).save(any(ReservationHistory.class));
        }

        @Test
        @DisplayName("(실패) 취소사유를 입력하지 않고 '입금대기' 상태에서 '취소' 상태로 변경한다")
        void failsToCancelReservationWithoutReason() {
            // given
            ReservationStatusUpdateRequest request = new ReservationStatusUpdateRequest(
                ReservationStatus.CANCELLED_BY_PHOTOGRAPHER, null);
            
            prepareMockReservationForm(memberId, formId, ReservationStatus.WAITING_FOR_DEPOSIT, isPhotographer);

            // when, then
            RestApiException exception = assertThrows(RestApiException.class, () -> {
                reservationService.updateReservationStatus(memberId, formId, request, isPhotographer);
            });

            assertEquals(CommonErrorCode.INVALID_PARAMETER, exception.getErrorCode());
            verify(mockReservationForm, never()).changeReservationStatus(request.getUpdateStatus());
            verify(reservationFormRepository, never()).save(mockReservationForm);
            verify(reservationHistoryRepository, never()).save(any(ReservationHistory.class));
        }

    }
}
