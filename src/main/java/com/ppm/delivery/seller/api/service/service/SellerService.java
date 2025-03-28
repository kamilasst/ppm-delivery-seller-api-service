package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.api.domain.request.BusinessHourDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerUpdateDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerUpdateDTOResponse;
import com.ppm.delivery.seller.api.service.api.interceptor.ContextHolder;
import com.ppm.delivery.seller.api.service.domain.mapper.SellerMapper;
import com.ppm.delivery.seller.api.service.domain.model.BusinessHour;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import com.ppm.delivery.seller.api.service.exception.BusinessException;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import com.ppm.delivery.seller.api.service.exception.EntityNotFoundException;
import com.ppm.delivery.seller.api.service.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
        seller.getContacts().forEach(contact -> contact.setSeller(seller));
        seller.getBusinessHours().forEach(businessHour -> businessHour.setSeller(seller));

        Seller savedSeller = sellerRepository.save(seller);

        return new SellerDTOResponse(savedSeller.getCode(), savedSeller.getStatus(), savedSeller.getAudit().getCreatedAt());

    }

    @Override
    public SellerUpdateDTOResponse update(String code, SellerUpdateDTORequest sellerUpdateDTORequest) {

        Optional<Seller> sellerOptional = sellerRepository.findByCode(code);
        validateExist(sellerOptional);

        Seller seller = sellerOptional.get();
        if (Objects.nonNull(sellerUpdateDTORequest.status())) {
            seller.setStatus(sellerUpdateDTORequest.status());
        }

        updateBusinessHour(sellerUpdateDTORequest, seller);

        LocalDateTime updateAt = LocalDateTime.now();
        seller.getAudit().setUpdatedAt(updateAt);

        Seller sevedSeller = sellerRepository.save(seller);

        return new SellerUpdateDTOResponse(sevedSeller.getCode(), sevedSeller.getStatus(), sevedSeller.getAudit().getUpdatedAt());

    }

    private static void updateBusinessHour(SellerUpdateDTORequest sellerUpdateDTORequest, Seller seller) {

        if (!CollectionUtils.isEmpty(sellerUpdateDTORequest.businessHours())) {

            List<BusinessHour> existingBusinessHours = seller.getBusinessHours();

            for (BusinessHourDTORequest dto : sellerUpdateDTORequest.businessHours()) {
                Optional<BusinessHour> existing = existingBusinessHours.stream()
                        .filter(bh -> bh.getDayOfWeek().equals(dto.dayOfWeek()))
                        .findFirst();

                if (existing.isPresent()) {
                    BusinessHour businessHour = existing.get();
                    businessHour.setOpenAt(dto.openAt());
                    businessHour.setCloseAt(dto.closeAt());
                } else {
                    BusinessHour newBusinessHour = BusinessHour.builder()
                            .dayOfWeek(dto.dayOfWeek())
                            .openAt(dto.openAt())
                            .closeAt(dto.closeAt())
                            .seller(seller)
                            .build();
                    existingBusinessHours.add(newBusinessHour);
                }
            }
        }
    }

    private void validateIdentificationCode(String identificationCode) {
        if (sellerRepository.existsByIdentificationCode(identificationCode)){
            throw new BusinessException(MessageErrorConstants.ERROR_IDENTIFICATION_CODE_ALREADY_EXISTS);
        }
    }

    private void validateExist(Optional<Seller> optionalSeller) {
        if (optionalSeller.isEmpty()){
            throw new EntityNotFoundException(MessageErrorConstants.ERROR_SELLER_NOT_FOUND);
        }
    }

}
