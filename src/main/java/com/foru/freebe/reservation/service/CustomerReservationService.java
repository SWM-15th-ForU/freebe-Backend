package com.foru.freebe.reservation.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.common.dto.SingleImageLink;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.errorcode.ProductErrorCode;
import com.foru.freebe.errors.errorcode.ProductImageErrorCode;
import com.foru.freebe.errors.errorcode.ProfileErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.product.dto.photographer.ProductComponentDto;
import com.foru.freebe.product.dto.photographer.ProductOptionDto;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.entity.ProductComponent;
import com.foru.freebe.product.entity.ProductImage;
import com.foru.freebe.product.respository.ProductComponentRepository;
import com.foru.freebe.product.respository.ProductImageRepository;
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
    private final ReservationFormRepository reservationFormRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;
    private final MemberRepository memberRepository;
    private final ProductDetailConvertor productDetailConvertor;
    private final ProductRepository productRepository;
    private final ProductComponentRepository productComponentRepository;
    private final ReferenceImageRepository referenceImageRepository;
    private final ReservationVerifier reservationVerifier;
    private final S3ImageService s3ImageService;
    private final ProfileRepository profileRepository;
    private final ProductImageRepository productImageRepository;

    @Transactional
    public Long registerReservationForm(Long id, FormRegisterRequest request, List<MultipartFile> images) throws
        IOException {

        Member customer = findMember(id);
        customer.assignInstagramId(request.getInstagramId());

        Member photographer = getMemberFromProfileName(request.getProfileName());

        Product product = getProductFromProductId(request.getProductId());

        ReservationForm reservationForm = buildReservationForm(product, request, photographer, customer);
        reservationVerifier.validateProductIsActive(product);
        ReservationForm newReservationForm = reservationFormRepository.save(reservationForm);

        ReservationHistory reservationHistory = ReservationHistory.createReservationHistory(newReservationForm,
            ReservationStatus.NEW);
        reservationHistoryRepository.save(reservationHistory);

        saveReferenceImages(request.getExistingImages(), images, reservationForm, customer.getId());

        return reservationForm.getId();
    }

    // TODO 추후 사진작가의 촬영 오픈일정 관련 로직이 추가되면 예약신청서 작성할 때 사진작가의 일정 조회 로직이 필요함
    public BasicReservationInfoResponse getBasicReservationForm(Long customerId, Long productId) {
        Member customer = findMember(customerId);

        Product product = getProductFromProductId(productId);

        List<ProductComponentDto> productComponentDtoList = productDetailConvertor.convertToProductComponentDtoList(
            product);
        List<ProductOptionDto> productOptionDtoList = productDetailConvertor.convertToProductOptionDtoList(product);

        return BasicReservationInfoResponse.builder()
            .name(customer.getName())
            .phoneNumber(customer.getPhoneNumber())
            .instagramId(customer.getInstagramId())
            .basicPrice(product.getBasicPrice())
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
            .basicPrice(reservationForm.getBasicPrice())
            .photoInfo(reservationForm.getPhotoInfo())
            .preferredDate(reservationForm.getPreferredDate())
            .photoOptions(reservationForm.getPhotoOption())
            .customerMemo(reservationForm.getCustomerMemo())
            .build();
    }

    private Member getMemberFromProfileName(String profileName) {
        Profile photographerProfile = profileRepository.findByProfileName(profileName)
            .orElseThrow(() -> new RestApiException(ProfileErrorCode.MEMBER_NOT_FOUND));

        return photographerProfile.getMember();
    }

    private Product getProductFromProductId(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new RestApiException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    private Member findMember(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    private void saveReferenceImages(List<String> existingImages, List<MultipartFile> images,
        ReservationForm reservationForm, Long customerId) throws IOException {

        int imageIndex = 0;
        for (String existingImage : existingImages) {
            if (existingImage == null) {
                uploadNewReferenceImage(images.get(imageIndex), reservationForm, customerId);
                imageIndex++;
            } else {
                useProductImageAsReference(reservationForm, existingImage);
            }
        }
    }

    private void uploadNewReferenceImage(MultipartFile image, ReservationForm reservationForm, Long customerId) throws
        IOException {

        SingleImageLink singleReferenceImage = s3ImageService.imageUploadToS3(image, S3ImageType.RESERVATION,
            customerId, true);

        ReferenceImage referenceImage = ReferenceImage.updateReferenceImage(singleReferenceImage.getOriginalUrl(),
            singleReferenceImage.getThumbnailUrl(), reservationForm);

        referenceImageRepository.save(referenceImage);
    }

    private void useProductImageAsReference(ReservationForm reservationForm, String existingImage) {
        ProductImage productImage = productImageRepository.findByThumbnailUrl(existingImage)
            .orElseThrow(() -> new RestApiException(ProductImageErrorCode.PRODUCT_IMAGE_NOT_FOUND));

        String originImage = productImage.getOriginUrl();
        ReferenceImage referenceImage = ReferenceImage.updateReferenceImage(originImage, existingImage,
            reservationForm);

        referenceImageRepository.save(referenceImage);
    }

    private ReservationForm buildReservationForm(Product product, FormRegisterRequest request, Member photographer,
        Member customer) {
        List<ProductComponent> productComponents = productComponentRepository.findByProduct(product);
        Map<String, String> photoInfo = getProductComponentsTitleAndContent(productComponents);

        ReservationForm.ReservationFormBuilder builder = ReservationForm.builder(photographer, customer,
                request.getInstagramId(), product.getTitle(), product.getBasicPrice(), request.getTotalPrice(),
                request.getServiceTermAgreement(), request.getPhotographerTermAgreement(), ReservationStatus.NEW)
            .photoInfo(photoInfo)
            .preferredDate(request.getPreferredDates())
            .photoOption(request.getPhotoOptions())
            .customerMemo(request.getCustomerMemo());
        return builder.build();
    }

    private Map<String, String> getProductComponentsTitleAndContent(List<ProductComponent> productComponents) {
        return productComponents.stream()
            .collect(Collectors.toMap(
                ProductComponent::getTitle,
                ProductComponent::getContent,
                (existing, replacement) -> {
                    throw new RestApiException(ProductErrorCode.COMPONENT_TITLE_ALREADY_EXISTS);
                }
            ));
    }
}
