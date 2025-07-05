package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.api.domain.request.BusinessHourDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerNearSearchRequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerUpdateDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerUpdateDTOResponse;
import com.ppm.delivery.seller.api.service.api.interceptor.ContextHolder;
import com.ppm.delivery.seller.api.service.domain.mapper.SellerMapper;
import com.ppm.delivery.seller.api.service.domain.model.BusinessHour;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import com.ppm.delivery.seller.api.service.exception.BusinessException;
import com.ppm.delivery.seller.api.service.exception.EntityNotFoundException;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import com.ppm.delivery.seller.api.service.repository.ISellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SellerService implements ISellerService {

    private final ContextHolder contextHolder;
    private final ISellerRepository sellerRepository;
    private final IPermissionService permissionService;

    public SellerService(final ContextHolder contextHolder,
                         final ISellerRepository sellerRepository,
                         final IPermissionService permissionService) {
        this.contextHolder = contextHolder;
        this.sellerRepository = sellerRepository;
        this.permissionService = permissionService;
    }

    @Override
    public SellerDTOResponse create(SellerDTORequest sellerDTORequest) {

        final String countryCode = contextHolder.getCountry();

        validateCreate(sellerDTORequest);

        Seller seller = createSeller(sellerDTORequest, countryCode);

        Seller savedSeller = sellerRepository.save(seller);

        return new SellerDTOResponse(savedSeller.getCode(), savedSeller.getStatus(), savedSeller.getAudit().getCreatedAt());

    }

    @Override
    public SellerUpdateDTOResponse update(String code, SellerUpdateDTORequest sellerUpdateDTORequest) {

        Optional<Seller> sellerOptional = sellerRepository.findByCode(code);
        validateUpdate(sellerUpdateDTORequest, sellerOptional);

        Seller seller = sellerOptional.get();

        updateStatus(sellerUpdateDTORequest.status(), seller);
        updateBusinessHours(sellerUpdateDTORequest.businessHours(), seller);

        Seller savedSeller = sellerRepository.save(seller);

        return new SellerUpdateDTOResponse(
                savedSeller.getCode(),
                savedSeller.getStatus(),
                savedSeller.getAudit().getUpdatedAt()
        );
    }

    @Override
    public List<Seller> getAvailableSellers(SellerNearSearchRequest request) {

        final String countryCode = contextHolder.getCountry();

        String dayOfWeek = request.orderCreateDate().getDayOfWeek().name();
        String orderHours = request.orderCreateDate().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        List<Seller> sellers = sellerRepository.findActiveSellersNear(
                request.orderDeliveryInfo().latitude(),
                request.orderDeliveryInfo().longitude(),
                request.radius(),
                dayOfWeek,
                orderHours,
                countryCode);

        return sellers.stream()
                .map(seller -> {
                    if (request.projections() == null || request.projections()
                            .isEmpty()) {
                        return seller;
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    private void validateCreate(SellerDTORequest sellerDTORequest) {

        validateRequestNull(sellerDTORequest);
        validateBusinessHoursEmpty(sellerDTORequest);
        validateIdentificationCode(sellerDTORequest.identification().code());
    }

    private static <T> void validateRequestNull(T request) {
        if (request == null) {
            throw new BusinessException(MessageErrorConstants.ERROR_REQUEST_BODY_IS_REQUIRED);
        }
    }


    private static void validateBusinessHoursEmpty(SellerDTORequest sellerDTORequest) {
        List<BusinessHourDTORequest> businessHours = sellerDTORequest.businessHours();
        if (businessHours != null && businessHours.isEmpty()) {
            throw new BusinessException(MessageErrorConstants.ERROR_AT_LEAST_ONE_BUSINESS_HOUR_REQUIRED);
        }
    }

    private void validateIdentificationCode(String identificationCode) {
        if (sellerRepository.existsByIdentificationCode(identificationCode)) {
            throw new BusinessException(MessageErrorConstants.ERROR_IDENTIFICATION_CODE_ALREADY_EXISTS);
        }
    }

    private static Seller createSeller(SellerDTORequest sellerDTORequest, String countryCode) {
        Seller seller = SellerMapper.INSTANCE.toEntity(sellerDTORequest);
        seller.setCode(UUID.randomUUID().toString());
        seller.setCountryCode(countryCode);
        seller.setStatus(Status.PENDING);

        seller.getContacts().forEach(contact -> contact.setSeller(seller));
        List<BusinessHour> businessHours = seller.getBusinessHours();

        if (Objects.nonNull(businessHours)) {
            businessHours.forEach(businessHour -> businessHour.setSeller(seller));
        }
        return seller;
    }

    private void validateExist(Optional<Seller> optionalSeller) {
        if (optionalSeller.isEmpty()) {
            throw new EntityNotFoundException(MessageErrorConstants.ERROR_SELLER_NOT_FOUND);
        }
    }

    private void validateUpdate(SellerUpdateDTORequest sellerUpdateDTORequest, Optional<Seller> sellerOptional) {

        validateRequestNull(sellerUpdateDTORequest);

        validateExist(sellerOptional);

        validateUpdateStatus(sellerUpdateDTORequest.status());
        validateUpdateStatusAnsBusinessHours(Objects.isNull(sellerUpdateDTORequest.status()), sellerUpdateDTORequest.businessHours());
    }

    private static void validateUpdateStatusAnsBusinessHours(boolean isStatusInvalid, List<BusinessHourDTORequest> businessHours) {
        boolean isBusinessHoursInvalid = CollectionUtils.isEmpty(businessHours);
        if (isStatusInvalid && isBusinessHoursInvalid) {
            throw new BusinessException(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED);
        }
    }

    private void validateUpdateStatus(Status status) {
        if (Objects.nonNull(status)) {
            permissionService.validateAdminAccess();
        }
    }

    private void updateStatus(Status status, Seller seller) {
        if (Objects.nonNull(status)) {
            seller.setStatus(status);
        }
    }

    private void updateBusinessHours(List<BusinessHourDTORequest> businessHoursDTOs, Seller seller) {
        if (CollectionUtils.isEmpty(businessHoursDTOs)) {
            return;
        }

        List<BusinessHour> existingBusinessHours = seller.getBusinessHours();

        for (BusinessHourDTORequest dto : businessHoursDTOs) {
            Optional<BusinessHour> existing = existingBusinessHours.stream()
                    .filter(bh -> bh.getDayOfWeek().equals(dto.getDayOfWeek().toString()))
                    .findFirst();

            if (existing.isPresent()) {
                BusinessHour businessHour = existing.get();
                businessHour.setOpenAt(dto.getOpenAt());
                businessHour.setCloseAt(dto.getCloseAt());
            } else {
                BusinessHour newBusinessHour = BusinessHour.builder()
                        .dayOfWeek(dto.getDayOfWeek().toString())
                        .openAt(dto.getOpenAt())
                        .closeAt(dto.getCloseAt())
                        .seller(seller)
                        .build();
                existingBusinessHours.add(newBusinessHour);
            }
        }
        seller.getAudit().setUpdatedAt(LocalDateTime.now());
    }

}
