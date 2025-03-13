package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.api.interceptor.ContextHolder;
import com.ppm.delivery.seller.api.service.builder.SellerBuilder;
import com.ppm.delivery.seller.api.service.builder.SellerDTORequestBuilder;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import com.ppm.delivery.seller.api.service.exception.BusinessException;
import com.ppm.delivery.seller.api.service.repository.SellerRepository;
import com.ppm.delivery.seller.api.service.utils.ConstantsMocks;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class SellerServiceTest {

    @InjectMocks
    private SellerService sellerService;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private ContextHolder contextHolder;

    @Test
    void shouldCreateSellerSuccessfully(){

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        Seller seller = SellerBuilder.create(request);

        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;

        Mockito.when(contextHolder.getCountry()).thenReturn(countryCode);
        Mockito.when(sellerRepository.isCodeExists(countryCode, request.identification().code())).thenReturn(false);
        Mockito.when(sellerRepository.save(Mockito.eq(countryCode), Mockito.any(Seller.class)))
                .thenAnswer(invocation -> {
                    Seller sellerToSave = invocation.getArgument(1);
                    sellerToSave.setCode(UUID.randomUUID().toString());
                    return sellerToSave;
                });

        ArgumentCaptor<Seller> sellerCaptor = ArgumentCaptor.forClass(Seller.class);

        SellerDTOResponse response = sellerService.create(request);

        seller.setCode(response.code());
        seller.setStatus(response.status());
        seller.getAudit().setCreateAt(response.createDate());

        Mockito.verify(sellerRepository).save(Mockito.eq(countryCode), sellerCaptor.capture());

        Seller savedSeller = sellerCaptor.getValue();
        Assertions.assertEquals(savedSeller.getCode(), response.code());
        Assertions.assertEquals(savedSeller.getStatus(), response.status());
        Assertions.assertEquals(savedSeller.getAudit().getCreateAt(), response.createDate());

        Assertions.assertEquals(savedSeller, seller);
    }

    @Test
    void shouldThrowExceptionWhenIdentificationCodeAlreadyExists(){

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;

        Mockito.when(contextHolder.getCountry()).thenReturn(countryCode);
        Mockito.when(sellerRepository.isCodeExists(countryCode, request.identification().code())).thenReturn(true);

        Assertions.assertThrows(BusinessException.class, () ->{
            sellerService.create(request);
        });

        Mockito.verify(sellerRepository, Mockito.never()).save(Mockito.eq(countryCode), Mockito.any(Seller.class));
    }

}
