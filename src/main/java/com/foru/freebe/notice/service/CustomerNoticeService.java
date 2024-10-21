package com.foru.freebe.notice.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.ProductErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.notice.dto.NoticeDto;
import com.foru.freebe.notice.repository.NoticeRepository;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.respository.ProductRepository;
import com.foru.freebe.product.service.ProductDetailConvertor;
import com.foru.freebe.profile.repository.ProfileRepository;
import com.foru.freebe.reservation.dto.PhotoNotice;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerNoticeService {

    private final ProfileRepository profileRepository;
    private final NoticeRepository noticeRepository;
    private final ProductRepository productRepository;
    private final ProductDetailConvertor productDetailConvertor;

    public List<NoticeDto> getNotices(Long productId) {

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RestApiException(ProductErrorCode.PRODUCT_NOT_FOUND));

        Map<String, PhotoNotice> photoNotice = product.getPhotoNotice();
        return productDetailConvertor.convertToNoticeDtoList(photoNotice);
    }
}
