package com.foru.freebe.reservation.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.common.service.S3ImageService;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.errorcode.ProductErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.product.dto.photographer.ProductComponentDto;
import com.foru.freebe.product.dto.photographer.ProductOptionDto;
import com.foru.freebe.product.entity.ActiveStatus;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.entity.ProductComponent;
import com.foru.freebe.product.entity.ProductOption;
import com.foru.freebe.product.respository.ProductComponentRepository;
import com.foru.freebe.product.respository.ProductOptionRepository;
import com.foru.freebe.product.respository.ProductRepository;
import com.foru.freebe.reservation.dto.BasicReservationInfoResponse;
import com.foru.freebe.reservation.dto.FormRegisterRequest;
import com.foru.freebe.reservation.entity.ReferenceImage;
import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.entity.ReservationHistory;
import com.foru.freebe.reservation.entity.ReservationStatus;
import com.foru.freebe.reservation.repository.ReferenceImageRepository;
import com.foru.freebe.reservation.repository.ReservationFormRepository;
import com.foru.freebe.reservation.repository.ReservationHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerReservationService {
    private final ReservationFormRepository reservationFormRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductComponentRepository productComponentRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ReferenceImageRepository referenceImageRepository;
    private final S3ImageService s3ImageService;

    public ApiResponse<String> registerReservationForm(Long customerId, FormRegisterRequest formRegisterRequest,
        List<MultipartFile> images) throws IOException {
        Member customer = findMember(customerId);
        Member photographer = findMember(formRegisterRequest.getPhotographerId());

        ReservationForm reservationForm = createReservationForm(formRegisterRequest, photographer, customer);
        validateReservationForm(formRegisterRequest);

        List<String> originalImageUrls = s3ImageService.uploadOriginalImage(images);
        List<String> thumbnailImageUrls = s3ImageService.uploadThumbnailImage(images);
        saveReservationForm(originalImageUrls, thumbnailImageUrls, reservationForm);

        return ApiResponse.<String>builder()
            .status(200)
            .message("Good Request")
            .data(null)
            .build();
    }

    // TODO 추후 사진작가의 촬영 오픈일정 관련 로직이 추가되면 예약신청서 작성할 때 사진작가의 일정 조회 로직이 필요함
    public ApiResponse<BasicReservationInfoResponse> getBasicReservationForm(Long customerId, Long productId) {
        Member customer = findMember(customerId);

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        List<ProductComponent> productComponents = productComponentRepository.findByProduct(product);
        List<ProductComponentDto> productComponentDtoList = convertProductComponentDtoList(
            productComponents);

        List<ProductOption> productOptions = productOptionRepository.findByProduct(product);
        List<ProductOptionDto> productOptionDtoList = convertProductOptionDtoList(productOptions);

        BasicReservationInfoResponse basicReservationInfoResponse = BasicReservationInfoResponse.builder()
            .name(customer.getName())
            .phoneNumber(customer.getPhoneNumber())
            .productComponentDtoList(productComponentDtoList)
            .productOptionDtoList(productOptionDtoList)
            .build();

        return ApiResponse.<BasicReservationInfoResponse>builder()
            .status(200)
            .message("Good Response")
            .data(basicReservationInfoResponse)
            .build();
    }

    private void saveReservationForm(List<String> originalImageUrls, List<String> thumbnailImageUrls,
        ReservationForm reservationForm) {
        ReservationForm newReservationForm = reservationFormRepository.save(reservationForm);

        reservationHistoryRepository.save(
            ReservationHistory.updateReservationStatus(newReservationForm, ReservationStatus.NEW));

        IntStream.range(0, originalImageUrls.size()).forEach(i -> {
            ReferenceImage referenceImage = ReferenceImage.updateReferenceImage(
                originalImageUrls.get(i),
                thumbnailImageUrls.get(i),
                reservationForm
            );
            referenceImageRepository.save(referenceImage);
        });
    }

    private Member findMember(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    private static ReservationForm createReservationForm(FormRegisterRequest request,
        Member photographer, Member customer) {
        ReservationForm.ReservationFormBuilder builder = ReservationForm.builder(photographer, customer,
                request.getInstagramId(), request.getProductTitle(), request.getTotalPrice(),
                request.getServiceTermAgreement(), request.getPhotographerTermAgreement(), ReservationStatus.NEW)
            .photoInfo(request.getPhotoInfo())
            .preferredDate(request.getPreferredDates())
            .photoOption(request.getPhotoOptions())
            .customerMemo(request.getCustomerMemo());
        return builder.build();
    }

    private void validateReservationForm(FormRegisterRequest formRegisterRequest) {
        validateProductTitleExists(formRegisterRequest.getProductTitle());
        validateProductIsActive(formRegisterRequest.getProductTitle());
    }

    private void validateProductIsActive(String productTitle) {
        Product product = productRepository.findByTitle(productTitle);
        if (product.getActiveStatus() != ActiveStatus.ACTIVE) {
            throw new RestApiException(ProductErrorCode.PRODUCT_INACTIVE_STATUS);
        }
    }

    private void validateProductTitleExists(String productTitle) {
        if (!productRepository.existsByTitle(productTitle)) {
            throw new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    private List<ProductOptionDto> convertProductOptionDtoList(List<ProductOption> productOptions) {
        List<ProductOptionDto> productOptionDtoList = new ArrayList<>();
        for (ProductOption productOption : productOptions) {
            ProductOptionDto productOptionDto = ProductOptionDto.builder()
                .title(productOption.getTitle())
                .price(productOption.getPrice())
                .description(productOption.getDescription())
                .build();
            productOptionDtoList.add(productOptionDto);
        }
        return productOptionDtoList;
    }

    private List<ProductComponentDto> convertProductComponentDtoList(List<ProductComponent> productComponents) {
        List<ProductComponentDto> productComponentDtoList = new ArrayList<>();
        for (ProductComponent productComponent : productComponents) {
            ProductComponentDto productComponentDto = ProductComponentDto.builder()
                .title(productComponent.getTitle())
                .content(productComponent.getContent())
                .description(productComponent.getDescription())
                .build();
            productComponentDtoList.add(productComponentDto);
        }
        return productComponentDtoList;
    }
}
