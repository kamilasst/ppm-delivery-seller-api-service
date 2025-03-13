package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.api.interceptor.ContextHolder;
import com.ppm.delivery.seller.api.service.domain.mapper.SellerMapper;
import com.ppm.delivery.seller.api.service.domain.model.Audit;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import com.ppm.delivery.seller.api.service.exception.BusinessException;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import com.ppm.delivery.seller.api.service.repository.SellerRepository;
import com.ppm.delivery.seller.api.service.utils.DateFormatterUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class SellerService implements ISellerService {

    private final ContextHolder contextHolder;
    private final SellerRepository sellerRepository;

    public SellerService(final ContextHolder contextHolder,
                         final SellerRepository sellerRepository){
        this.contextHolder = contextHolder;
        this.sellerRepository = sellerRepository;
    }

    @Override
    public SellerDTOResponse create(SellerDTORequest sellerDTORequest) {

        final String countryCode = contextHolder.getCountry();

        validateIdentificationCode(countryCode, sellerDTORequest.identification().code());

        Seller seller = SellerMapper.INSTANCE.toEntity(sellerDTORequest);
        seller.setCode(UUID.randomUUID().toString());
        seller.setStatus(Status.PENDING);
        seller.setAudit(Audit.builder().createAt(DateFormatterUtil.format(Instant.now())).build());
        seller.getContacts().forEach(contact -> contact.setSeller(seller));
        seller.getBusinessHours().forEach(businessHour -> businessHour.setSeller(seller));

        Seller sellerSaved = sellerRepository.save(countryCode, seller);

        return new SellerDTOResponse(sellerSaved.getCode(), sellerSaved.getStatus(), sellerSaved.getAudit().getCreateAt());
    }

    private void validateIdentificationCode(String countryCode, String code) {
        if (sellerRepository.isCodeExists(countryCode, code)){
            throw new BusinessException(MessageErrorConstants.ERROR_IDENTIFICATION_CODE_ALREADY_EXISTS);
        }
    }

}
