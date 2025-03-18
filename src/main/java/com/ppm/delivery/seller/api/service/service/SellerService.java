package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.api.domain.request.BusinessHourDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerUpdateDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerUpdateDTOResponse;
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
import java.util.ArrayList;
import java.util.List;
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

        validateIdentificationCode(sellerDTORequest.identification().code());

        Seller seller = SellerMapper.INSTANCE.toEntity(sellerDTORequest);
        seller.setCode(UUID.randomUUID().toString());
        seller.setCountryCode(countryCode);
        seller.setStatus(Status.PENDING);
        seller.setAudit(Audit.builder().createAt(DateFormatterUtil.format(Instant.now())).build());
        seller.getContacts().forEach(contact -> contact.setSeller(seller));
        seller.getBusinessHours().forEach(businessHour -> businessHour.setSeller(seller));

        sellerRepository.save(seller);

        return new SellerDTOResponse(seller.getCode(), seller.getStatus(), seller.getAudit().getCreateAt());
    }

    @Override
    public SellerUpdateDTOResponse update(String code, SellerUpdateDTORequest sellerUpdateDTORequest) {

        Seller seller = validateCode(code);

        String updateAt = DateFormatterUtil.format(Instant.now());

        sellerUpdateDTORequest.status().ifPresent(seller::setStatus);

        List<BusinessHourDTORequest> businessHoursDTO = new ArrayList<>();
        sellerUpdateDTORequest.businessHours().ifPresent(businessHoursDTO::addAll);

        return new SellerUpdateDTOResponse(
                seller.getCode(),
                updateAt,
                seller.getStatus(),
                businessHoursDTO
        );
    }

    private void validateIdentificationCode(String IdentificationCode) {
        if (sellerRepository.existsByIdentificationCode(IdentificationCode)){
            throw new BusinessException(MessageErrorConstants.ERROR_IDENTIFICATION_CODE_ALREADY_EXISTS);
        }
    }

    private Seller validateCode(String code) {
        Seller seller = sellerRepository.findByCode(code);
        if (seller == null){
            throw new BusinessException(MessageErrorConstants.ERROR_IDENTIFICATION_CODE_ALREADY_EXISTS);
        }
        return seller;
    }

}
