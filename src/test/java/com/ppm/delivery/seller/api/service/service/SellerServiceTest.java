package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.builder.SellerBuilder;
import com.ppm.delivery.seller.api.service.builder.SellerDTORequestBuilder;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import com.ppm.delivery.seller.api.service.exception.BusinessException;
import com.ppm.delivery.seller.api.service.repository.SellerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SellerServiceTest {

    @InjectMocks
    private SellerService sellerService;

    @Mock
    private SellerRepository sellerRepository;

    @Test
    void shouldCreateSellerSuccessfully(){

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        Seller seller = SellerBuilder.create(request);

        Mockito.when(sellerRepository.existsByIdentificationCode(request.identification().code())).thenReturn(false);
        Mockito.when(sellerRepository.save(Mockito.any(Seller.class))).thenReturn(seller);

        ArgumentCaptor<Seller> sellerCaptor = ArgumentCaptor.forClass(Seller.class);

        SellerDTOResponse response = sellerService.create(request);

        seller.setCode(response.code());
        seller.setStatus(response.status());
        seller.getAudit().setCreateAt(response.createDate());

        Mockito.verify(sellerRepository).save(sellerCaptor.capture());
        Seller savedSeller = sellerCaptor.getValue();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(seller, savedSeller);
        System.out.println(seller);
        System.out.println(savedSeller);

    }

    @Test
    void shouldThrowExceptionWhenIdentificationCodeAlreadyExists(){

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();

        Mockito.when(sellerRepository.existsByIdentificationCode(request.identification().code())).thenReturn(true);

        Assertions.assertThrows(BusinessException.class, () ->{
            sellerService.create(request);
        });

        Mockito.verify(sellerRepository, Mockito.never()).save(Mockito.any(Seller.class));
    }

}
