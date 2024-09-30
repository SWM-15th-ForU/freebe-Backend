package com.foru.freebe.reservation.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.common.dto.ImageLinkSet;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.product.dto.photographer.ProductComponentDto;
import com.foru.freebe.product.dto.photographer.ProductOptionDto;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.respository.ProductRepository;
import com.foru.freebe.product.service.ProductDetailConvertor;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.repository.ProfileRepository;
import com.foru.freebe.reservation.dto.BasicReservationInfoResponse;
import com.foru.freebe.reservation.dto.FormRegisterRequest;
import com.foru.freebe.reservation.dto.ReservationInfoResponse;
import com.foru.freebe.reservation.entity.ReferenceImage;
import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.entity.ReservationHistory;
import com.foru.freebe.reservation.entity.ReservationStatus;
import com.foru.freebe.reservation.repository.ReferenceImageRepository;
import com.foru.freebe.reservation.repository.ReservationFormRepository;
import com.foru.freebe.reservation.repository.ReservationHistoryRepository;
import com.foru.freebe.s3.S3ImageService;
import com.foru.freebe.s3.S3ImageType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerReservationService {
    private static final int REFERENCE_THUMBNAIL_SIZE = 200;

    private final ReservationFormRepository reservationFormRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;
    private final MemberRepository memberRepository;
    private final ProductDetailConvertor productDetailConvertor;
    private final ProductRepository productRepository;
    private final ReferenceImageRepository referenceImageRepository;
    private final ReservationVerifier reservationVerifier;
    private final S3ImageService s3ImageService;
    private final ProfileRepository profileRepository;

    @Transactional
    public Long registerReservationForm(Long id, FormRegisterRequest request, List<MultipartFile> images) throws
        IOException {

        Member customer = findMember(id);
        customer.assignInstagramId(request.getInstagramId());

        Profile photographerProfile = profileRepository.findByProfileName(request.getProfileName())
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
        Member photographer = photographerProfile.getMember();

        ReservationForm reservationForm = buildReservationForm(request, photographer, customer);
        reservationVerifier.validateReservationFormBeforeSave(request);
        ReservationForm newReservationForm = reservationFormRepository.save(reservationForm);

        ReservationHistory reservationHistory = ReservationHistory.createReservationHistory(newReservationForm,
            ReservationStatus.NEW);
        reservationHistoryRepository.save(reservationHistory);

        saveReferenceImages(images, reservationForm, customer.getId());

        return reservationForm.getId();
    }

    // TODO 추후 사진작가의 촬영 오픈일정 관련 로직이 추가되면 예약신청서 작성할 때 사진작가의 일정 조회 로직이 필요함
    public BasicReservationInfoResponse getBasicReservationForm(Long customerId, Long productId) {
        Member customer = findMember(customerId);

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        List<ProductComponentDto> productComponentDtoList = productDetailConvertor.convertToProductComponentDtoList(
            product);
        List<ProductOptionDto> productOptionDtoList = productDetailConvertor.convertToProductOptionDtoList(product);

        return BasicReservationInfoResponse.builder()
            .name(customer.getName())
            .phoneNumber(customer.getPhoneNumber())
            .instagramId(customer.getInstagramId())
            .productComponentDtoList(productComponentDtoList)
            .productOptionDtoList(productOptionDtoList)
            .build();
    }

    public ReservationInfoResponse getReservationInfo(Long formId, Long customerId) {
        ReservationForm reservationForm = reservationFormRepository.findById(formId)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        reservationVerifier.validateCustomerAccess(reservationForm, customerId);

        return ReservationInfoResponse.builder()
            .reservationStatus(reservationForm.getReservationStatus())
            .productTitle(reservationForm.getProductTitle())
            .photoInfo(reservationForm.getPhotoInfo())
            .preferredDate(reservationForm.getPreferredDate())
            .photoOptions(reservationForm.getPhotoOption())
            .customerMemo(reservationForm.getCustomerMemo())
            .build();
    }

    private void saveReferenceImages(List<MultipartFile> images, ReservationForm reservationForm, Long id) throws
        IOException {

        ImageLinkSet imageLinkSet = s3ImageService.imageUploadToS3(images, S3ImageType.RESERVATION, id,
            REFERENCE_THUMBNAIL_SIZE);

        IntStream.range(0, imageLinkSet.getOriginUrl().size()).forEach(i -> {
            ReferenceImage referenceImage = ReferenceImage.updateReferenceImage(
                imageLinkSet.getOriginUrl().get(i), imageLinkSet.getThumbnailUrl().get(i), reservationForm);
            referenceImageRepository.save(referenceImage);
        });
    }

    private Member findMember(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    private ReservationForm buildReservationForm(FormRegisterRequest request, Member photographer, Member customer) {
        ReservationForm.ReservationFormBuilder builder = ReservationForm.builder(photographer, customer,
                request.getInstagramId(), request.getProductTitle(), request.getTotalPrice(),
                request.getServiceTermAgreement(), request.getPhotographerTermAgreement(), ReservationStatus.NEW)
            .photoInfo(request.getPhotoInfo())
            .preferredDate(request.getPreferredDates())
            .photoOption(request.getPhotoOptions())
            .customerMemo(request.getCustomerMemo());
        return builder.build();
    }
}
