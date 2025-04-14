package com.ppm.delivery.seller.api.service.controller;

import com.ppm.delivery.seller.api.service.api.controller.SellerController;
import com.ppm.delivery.seller.api.service.api.domain.request.BusinessHourDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerUpdateDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerUpdateDTOResponse;
import com.ppm.delivery.seller.api.service.builder.SellerBuilder;
import com.ppm.delivery.seller.api.service.builder.SellerDTORequestBuilder;
import com.ppm.delivery.seller.api.service.builder.SellerDTOResponseBuilder;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import com.ppm.delivery.seller.api.service.exception.BusinessException;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import com.ppm.delivery.seller.api.service.service.SellerService;
import com.ppm.delivery.seller.api.service.utils.ConstantsMocks;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class SellerControllerTest {

    @Mock
    private SellerService sellerService;

    @InjectMocks
    private SellerController sellerController;

    @Test
    void shouldCreateSellerSuccessfully(){

        //Arrange
        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.create(countryCode, request);
        SellerDTOResponse response = SellerDTOResponseBuilder.create(seller);

        Mockito.when(sellerService.create(request)).thenReturn(response);

        //Act
        ResponseEntity<SellerDTOResponse> result = sellerController.create(request);

        //Assert
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
        Assertions.assertEquals(response,result.getBody());
        Mockito.verify(sellerService, Mockito.times(1)).create(request);

    }

    @Test
    void shouldThrowExceptionWhenIdentificationCodeAlreadyExists(){

        //Arrange
        SellerDTORequest request = SellerDTORequestBuilder.createDefault();

        Mockito.when(sellerService.create(request))
                .thenThrow(new BusinessException(MessageErrorConstants.ERROR_IDENTIFICATION_CODE_ALREADY_EXISTS));

        //Act
        BusinessException exception = Assertions.assertThrows(
                BusinessException.class,
                () -> sellerController.create(request)
        );

        //Assert
        Assertions.assertEquals(MessageErrorConstants.ERROR_IDENTIFICATION_CODE_ALREADY_EXISTS, exception.getMessage());
        Mockito.verify(sellerService, Mockito.times(1)).create(request);
    }

    @Test
    public void shouldSuccessfullyPatchSellerStatus(){

        //Arrange
        String sellerCode = UUID.randomUUID().toString();
        SellerUpdateDTORequest sellerUpdateDTORequest = SellerUpdateDTORequest.builder()
                .status(Status.ACTIVE).build();

        SellerUpdateDTOResponse response = new SellerUpdateDTOResponse(sellerCode, Status.ACTIVE, LocalDateTime.now());
        Mockito.when(sellerService.update(sellerCode, sellerUpdateDTORequest)).thenReturn(response);

        //Act
        ResponseEntity<SellerUpdateDTOResponse> responseEntity = sellerController.patchV1(sellerCode, sellerUpdateDTORequest);

        //Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(sellerCode, responseEntity.getBody().code());
        Assertions.assertEquals(Status.ACTIVE, responseEntity.getBody().status());

        Mockito.verify(sellerService, Mockito.times(1)).update(sellerCode, sellerUpdateDTORequest);
    }

    @Test
    public void shouldSuccessfullyPatchSellerBusinessHours(){

        //Arrange
        String sellerCode = UUID.randomUUID().toString();

        List<BusinessHourDTORequest> businessHoursList  = List.of(
                BusinessHourDTORequest.builder()
                        .dayOfWeek("SUNDAY")
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_2)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_3).build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek("MONDAY")
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_3)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_3).build());

        SellerUpdateDTORequest sellerUpdateDTORequest = SellerUpdateDTORequest.builder()
                .businessHours(businessHoursList)
                .build();
        SellerUpdateDTOResponse response = new SellerUpdateDTOResponse(sellerCode, Status.PENDING, LocalDateTime.now());

        Mockito.when(sellerService.update(sellerCode, sellerUpdateDTORequest)).thenReturn(response);

        //Act
        ResponseEntity<SellerUpdateDTOResponse> responseEntity = sellerController.patchV1(sellerCode, sellerUpdateDTORequest);

        //Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(sellerCode, responseEntity.getBody().code());
        Assertions.assertEquals(Status.PENDING, responseEntity.getBody().status());

        Mockito.verify(sellerService, Mockito.times(1)).update(sellerCode, sellerUpdateDTORequest);
    }

    @Test
    public void shouldSuccessfullyPatchSellerStatusAndBusinessHour(){

        //Arrange
        String sellerCode = UUID.randomUUID().toString();

        List<BusinessHourDTORequest> businessHoursList  = List.of(
                BusinessHourDTORequest.builder()
                        .dayOfWeek("SUNDAY")
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_2)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_3).build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek("MONDAY")
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_3)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_3).build());

        SellerUpdateDTORequest sellerUpdateDTORequest = SellerUpdateDTORequest.builder()
                .businessHours(businessHoursList)
                .status(Status.ACTIVE).build();

        SellerUpdateDTOResponse response = new SellerUpdateDTOResponse(sellerCode, Status.ACTIVE, LocalDateTime.now());

        Mockito.when(sellerService.update(sellerCode, sellerUpdateDTORequest)).thenReturn(response);

        //Act
        ResponseEntity<SellerUpdateDTOResponse> responseEntity = sellerController.patchV1(sellerCode, sellerUpdateDTORequest);

        //Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(sellerCode, responseEntity.getBody().code());
        Assertions.assertEquals(Status.ACTIVE, responseEntity.getBody().status());

        Mockito.verify(sellerService, Mockito.times(1)).update(sellerCode, sellerUpdateDTORequest);
    }

}
