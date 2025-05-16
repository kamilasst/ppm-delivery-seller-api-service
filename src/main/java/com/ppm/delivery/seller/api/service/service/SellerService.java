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

        // TODO atg ReviewCode POST: Por favor avalie renomear o metodo validateCreateRequest para validateCreate, dentro dele chamar o validateIdentificationCode
        // isso ajuda a concentrar todas as validações de criação em um único lugar
        validateCreateRequest(sellerDTORequest);
        validateIdentificationCode(sellerDTORequest.identification().code());

        // TODO atg ReviewCode POST: Por favor avalie criar um método createSeller responsável por criar o seller. linha 53 até 64
        Seller seller = SellerMapper.INSTANCE.toEntity(sellerDTORequest);
        seller.setCode(UUID.randomUUID().toString());
        seller.setCountryCode(countryCode);
        seller.setStatus(Status.PENDING);

        seller.getContacts().forEach(contact -> contact.setSeller(seller));
        List<BusinessHour> businessHours = seller.getBusinessHours();

        // TODO atg ReviewCode POST: Por favor avalie  if (Objects.nonNull(businessHours))
        if (businessHours != null) {
            businessHours.forEach(businessHour -> businessHour.setSeller(seller));
        }

        Seller savedSeller = sellerRepository.save(seller);

        return new SellerDTOResponse(savedSeller.getCode(), savedSeller.getStatus(), savedSeller.getAudit().getCreatedAt());

    }

    @Override
    public SellerUpdateDTOResponse update(String code, SellerUpdateDTORequest sellerUpdateDTORequest) {

        // TODO atg ReviewCode PATCH: Por favor avalie reorganizar esses metodos de valdiacao da seguinte forma
        // Renomear validateUpdateRequest para validateUpdate e adicionar o validateUpdateStatus dentro dele
        // para concentrar as validacoes de negócio dentro de um unico metodo
        // Se ficar na duvida, chama... podemos avaliar se faz sentido o validateExist ir para dentro dele tb
        validateUpdateRequest(sellerUpdateDTORequest);

        Optional<Seller> sellerOptional = sellerRepository.findByCode(code);
        validateExist(sellerOptional);
        validateUpdateStatus(sellerUpdateDTORequest.status());

        Seller seller = sellerOptional.get();

        // TODO atg ReviewCode PATCH: Evite utilizar classe como nomes genericos: 'helper'
        // TODO atg ReviewCode PATCH: Não achei interessante criar um service a parte(@Component) para fazer essa validacao em outra classe
        // achei um pouco confuso, minha sugestao é ser um método privado dentro dessa classe mesmo, podemos trocar ideia sobre
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
        // TODO atg ReviewCode POST: Por favor avalie criar um método validateRequestNull
        if (sellerDTORequest == null) {
            throw new BusinessException(MessageErrorConstants.ERROR_REQUEST_BODY_IS_REQUIRED);
        }

        // TODO atg ReviewCode POST: Por favor avalie criar um método validateBusinessHoursEmpty
        List<BusinessHourDTORequest> businessHours = sellerDTORequest.businessHours();
        if (businessHours != null && businessHours.isEmpty()) {
            throw new BusinessException(MessageErrorConstants.ERROR_AT_LEAST_ONE_BUSINESS_HOUR_REQUIRED);
        }
    }

    private void validateUpdateRequest(SellerUpdateDTORequest sellerUpdateDTORequest) {

        // TODO atg ReviewCode PATCH: Por favor avalie criar um método validateRequestNull, como esse método é muito
        // parecido com o do POST, de uma pesquisada e avalie utilizar "generics" para criar um método generico e
        // reutilizar o metodo no POST e PATCH
        if (sellerUpdateDTORequest == null) {
            throw new BusinessException(MessageErrorConstants.ERROR_REQUEST_BODY_IS_REQUIRED);
        }

        // TODO atg ReviewCode PATCH: Por favor avalie criar um método validateUpdateBusinessHours
        boolean isStatusInvalid = Objects.isNull(sellerUpdateDTORequest.status());
        boolean isBusinessHoursInvalid = CollectionUtils.isEmpty(sellerUpdateDTORequest.businessHours());
        if (isStatusInvalid && isBusinessHoursInvalid) {
            throw new BusinessException(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED);
        }

    }

    private void validateUpdateStatus(Status status) {
        if (Objects.nonNull(status)) {
            permissionService.validateAdminAccess();
        }
    }

}
