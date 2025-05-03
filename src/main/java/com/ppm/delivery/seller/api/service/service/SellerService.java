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
import com.ppm.delivery.seller.api.service.exception.EntityNotFoundException;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import com.ppm.delivery.seller.api.service.exception.RequiredFieldsException;
import com.ppm.delivery.seller.api.service.repository.ISellerRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class SellerService implements ISellerService {

    private final ContextHolder contextHolder;
    private final ISellerRepository sellerRepository;
    private final IPermissionService permissionService;
    private final SellerUpdateHelper sellerUpdateHelper;

    public SellerService(final ContextHolder contextHolder,
                         final ISellerRepository sellerRepository,
                         final IPermissionService permissionService,
                         final SellerUpdateHelper sellerUpdateHelper) {
        this.contextHolder = contextHolder;
        this.sellerRepository = sellerRepository;
        this.permissionService = permissionService;
        this.sellerUpdateHelper = sellerUpdateHelper;
    }

    @Override
    public SellerDTOResponse create(SellerDTORequest sellerDTORequest) {

        final String countryCode = contextHolder.getCountry();

        validateCreateRequest(sellerDTORequest);
        validateIdentificationCode(sellerDTORequest.identification().code());

        Seller seller = SellerMapper.INSTANCE.toEntity(sellerDTORequest);
        seller.setCode(UUID.randomUUID().toString());
        seller.setCountryCode(countryCode);
        seller.setStatus(Status.PENDING);

        seller.getContacts().forEach(contact -> contact.setSeller(seller));
        List<BusinessHour> businessHours = seller.getBusinessHours();
        if (businessHours != null) {
            businessHours.forEach(businessHour -> businessHour.setSeller(seller));
        }

        Seller savedSeller = sellerRepository.save(seller);

        return new SellerDTOResponse(savedSeller.getCode(), savedSeller.getStatus(), savedSeller.getAudit().getCreatedAt());

    }

    @Override
    public SellerUpdateDTOResponse update(String code, SellerUpdateDTORequest sellerUpdateDTORequest) {

        validateUpdateRequest(sellerUpdateDTORequest);

        Optional<Seller> sellerOptional = sellerRepository.findByCode(code);
        validateExist(sellerOptional);
        validateUpdateStatus(sellerUpdateDTORequest.status());

        Seller seller = sellerOptional.get();
        sellerUpdateHelper.updateStatus(sellerUpdateDTORequest.status(), seller);
        sellerUpdateHelper.updateBusinessHours(sellerUpdateDTORequest.businessHours(), seller);

        Seller savedSeller = sellerRepository.save(seller);

        return new SellerUpdateDTOResponse(
                savedSeller.getCode(),
                savedSeller.getStatus(),
                savedSeller.getAudit().getUpdatedAt()
        );
    }

    private void validateIdentificationCode(String identificationCode) {
        if (sellerRepository.existsByIdentificationCode(identificationCode)){
            throw new BusinessException(MessageErrorConstants.ERROR_IDENTIFICATION_CODE_ALREADY_EXISTS);
        }
    }

    private void validateExist(Optional<Seller> optionalSeller) {
        if (optionalSeller.isEmpty()) {
            throw new EntityNotFoundException(MessageErrorConstants.ERROR_SELLER_NOT_FOUND);
        }
    }

    private void validateCreateRequest(SellerDTORequest sellerDTORequest) {
        if (sellerDTORequest == null) {
            throw new BusinessException(MessageErrorConstants.ERROR_REQUEST_BODY_IS_REQUIRED);
        }

        List<BusinessHourDTORequest> businessHours = sellerDTORequest.businessHours();

        if (businessHours != null && businessHours.isEmpty()) {
            throw new BusinessException(MessageErrorConstants.ERROR_AT_LEAST_ONE_BUSINESS_HOUR_REQUIRED);
        }

        if (businessHours != null) {
            List<String> errorMessage = new ArrayList<>();
            for (BusinessHourDTORequest dto : businessHours) {
                if (StringUtils.isBlank(dto.dayOfWeek())) {
                    errorMessage.add("Dia da Semana obrigatorio");
                }
                if (StringUtils.isBlank(dto.openAt())) {
                    errorMessage.add("Horario de abertura obrigatorio");
                }
                if (StringUtils.isBlank(dto.closeAt())) {
                    errorMessage.add("Horario de fechamento obrigatorio");
                }
            }
            if (!errorMessage.isEmpty()) {
                throw new RequiredFieldsException(errorMessage);
            }
        }

    }


    private void validateUpdateRequest(SellerUpdateDTORequest sellerUpdateDTORequest) {

        if (sellerUpdateDTORequest == null) {
            throw new BusinessException(MessageErrorConstants.ERROR_REQUEST_BODY_IS_REQUIRED);
        }
        boolean isStatusInvalid = Objects.isNull(sellerUpdateDTORequest.status());
        boolean isBusinessHoursInvalid = CollectionUtils.isEmpty(sellerUpdateDTORequest.businessHours());

        if (isStatusInvalid && isBusinessHoursInvalid) {
            throw new BusinessException(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED);
        }

        List<BusinessHourDTORequest> businessHours = sellerUpdateDTORequest.businessHours();

        if (businessHours != null) {
            List<String> errorMessages = new ArrayList<>();
            for (BusinessHourDTORequest dto : businessHours) {
                if (StringUtils.isBlank(dto.dayOfWeek())) {
                    errorMessages.add("Dia da semana obrigatório.");
                }
                if (StringUtils.isBlank(dto.openAt())) {
                    errorMessages.add("Horário de abertura obrigatório.");
                }
                if (StringUtils.isBlank(dto.closeAt())) {
                    errorMessages.add("Horário de fechamento obrigatório.");
                }
            }

            if (!errorMessages.isEmpty()) {
                throw new RequiredFieldsException(errorMessages);
            }
        }
    }

    private void validateUpdateStatus(Status status) {
        if (Objects.nonNull(status)) {
            permissionService.validateAdminAccess();
        }
    }

}
