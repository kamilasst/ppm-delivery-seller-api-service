package com.ppm.delivery.seller.api.service.controller;

import com.ppm.delivery.seller.api.service.api.controller.SellerController;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.builder.SellerBuilder;
import com.ppm.delivery.seller.api.service.builder.SellerDTORequestBuilder;
import com.ppm.delivery.seller.api.service.builder.SellerDTOResponseBuilder;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import com.ppm.delivery.seller.api.service.exception.BusinessException;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import com.ppm.delivery.seller.api.service.service.SellerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class SellerControllerTest {

    @Mock
    private SellerService sellerService;

    @InjectMocks
    private SellerController sellerController;

    @Test
    void shouldCreateSellerSuccessfully(){

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        Seller seller = SellerBuilder.create(request);
        SellerDTOResponse response = SellerDTOResponseBuilder.create(seller);

        Mockito.when(sellerService.create(request)).thenReturn(response);

        ResponseEntity<SellerDTOResponse> result = sellerController.create(request);

        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
        Assertions.assertEquals(response,result.getBody());
        Mockito.verify(sellerService, Mockito.times(1)).create(request);

    }

    @Test
    void shouldThrowExceptionWhenIdentificationCodeAlreadyExists(){

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();

        Mockito.when(sellerService.create(request))
                .thenThrow(new BusinessException(MessageErrorConstants.ERROR_IDENTIFICATION_CODE_ALREADY_EXISTS));

        BusinessException exception = Assertions.assertThrows(
                BusinessException.class,
                () -> sellerController.create(request)
        );

        Assertions.assertEquals(MessageErrorConstants.ERROR_IDENTIFICATION_CODE_ALREADY_EXISTS, exception.getMessage());
        Mockito.verify(sellerService, Mockito.times(1)).create(request);
    }

}
