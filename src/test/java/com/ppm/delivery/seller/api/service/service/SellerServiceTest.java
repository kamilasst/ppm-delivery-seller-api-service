package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.api.domain.request.BusinessHourDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerUpdateDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerUpdateDTOResponse;
import com.ppm.delivery.seller.api.service.api.interceptor.ContextHolder;
import com.ppm.delivery.seller.api.service.builder.SellerBuilder;
import com.ppm.delivery.seller.api.service.builder.SellerDTORequestBuilder;
import com.ppm.delivery.seller.api.service.domain.model.BusinessHour;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import com.ppm.delivery.seller.api.service.domain.profile.Profile;
import com.ppm.delivery.seller.api.service.exception.BusinessException;
import com.ppm.delivery.seller.api.service.exception.EntityNotFoundException;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import com.ppm.delivery.seller.api.service.repository.ISellerRepository;
import com.ppm.delivery.seller.api.service.utils.ConstantsMocks;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class SellerServiceTest {

    @InjectMocks
    private SellerService sellerService;
    @Mock
    private ISellerRepository sellerRepository;
    @Mock
    private ContextHolder contextHolder;
    @Mock
    private PermissionService permissionValidator;

    @BeforeEach
    void setUp() {
        permissionValidator = new PermissionService(contextHolder);
        sellerService = new SellerService(contextHolder, sellerRepository, permissionValidator);
    }

    @Test
    void shouldCreateSellerSuccessfully() {

        //Arrange
        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.create(countryCode, request);

        Mockito.when(contextHolder.getCountry()).thenReturn(countryCode);
        Mockito.when(sellerRepository.existsByIdentificationCode(seller.getIdentification().getCode())).thenReturn(false);

        ArgumentCaptor<Seller> sellerCaptor = ArgumentCaptor.forClass(Seller.class);
        Mockito.when(sellerRepository.save(Mockito.any(Seller.class)))
                .thenAnswer(invocation -> {
                    Seller sellerToSave = invocation.getArgument(0);
                    sellerToSave.setCode(UUID.randomUUID().toString());
                    sellerToSave.getAudit().setCreatedAt(LocalDateTime.now());
                    return sellerToSave;
                });

        //Act
        SellerDTOResponse response = sellerService.create(request);

        //Assert
        seller.setCode(response.code());
        seller.setStatus(response.status());
        seller.getAudit().setCreatedAt(response.createdDate());

        Mockito.verify(sellerRepository).save(sellerCaptor.capture());

        Seller savedSeller = sellerCaptor.getValue();
        Assertions.assertEquals(savedSeller.getCode(), response.code());
        Assertions.assertEquals(savedSeller.getStatus(), response.status());
        Assertions.assertEquals(savedSeller.getAudit().getCreatedAt(), response.createdDate());

        Assertions.assertEquals(savedSeller, seller);

    }

    @Test
    void shouldThrowBusinessExceptionWhenIdentificationCodeAlreadyExists() {

        //Arrange
        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.create(countryCode, request);

        Mockito.when(contextHolder.getCountry()).thenReturn(countryCode);
        Mockito.when(sellerRepository.existsByIdentificationCode(seller.getIdentification().getCode())).thenReturn(true);

        Assertions.assertThrows(BusinessException.class, () -> sellerService.create(request));
        Mockito.verify(sellerRepository, Mockito.never()).save(Mockito.any(Seller.class));

    }

    @Test
    void shouldSuccessfullyPatchOnlySellerStatus() {

        //Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);

        Mockito.when(sellerRepository.findByCode(seller.getCode())).thenReturn(Optional.of(seller));
        Mockito.when(contextHolder.getProfile()).thenReturn(Profile.ADMIN.name());
        Mockito.when(sellerRepository.save(Mockito.any(Seller.class)))
                .thenAnswer(invocation -> {
                    Seller saved = invocation.getArgument(0);
                    saved.getAudit().setUpdatedAt(LocalDateTime.now());
                    return saved;
                });

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(Status.ACTIVE).build();

        //Act
        SellerUpdateDTOResponse response = sellerService.update(seller.getCode(), request);

        //Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(seller.getCode(), response.code());
        Assertions.assertEquals(Status.ACTIVE, response.status());
        Assertions.assertNotNull(response.updatedDate());
        Assertions.assertTrue(response.updatedDate().isAfter(seller.getAudit().getCreatedAt()));

        Mockito.verify(sellerRepository, Mockito.times(1)).findByCode(seller.getCode());
        Mockito.verify(contextHolder, Mockito.times(1)).getProfile();
        Mockito.verify(sellerRepository, Mockito.times(1)).save(seller);

    }

    @Test
    void shouldSuccessfullyPatchOnlySellerBusinessHours() {

        //Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);

        Mockito.when(sellerRepository.findByCode(seller.getCode())).thenReturn(Optional.of(seller));
        Mockito.when(sellerRepository.save(Mockito.any(Seller.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<BusinessHourDTORequest> businessHoursList  = List.of(
                BusinessHourDTORequest.builder()
                        .dayOfWeek("SUNDAY")
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_2)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_2).build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek("MONDAY")
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_3)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_3).build());

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHoursList)
                .build();

        //Act
        SellerUpdateDTOResponse response = sellerService.update(seller.getCode(), request);

        //Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(seller.getCode(), response.code());
        Assertions.assertNotNull(response.updatedDate());
        Assertions.assertTrue(response.updatedDate().isAfter(seller.getAudit().getCreatedAt()));

        Assertions.assertEquals(2, seller.getBusinessHours().size());

        BusinessHour updatedSunday = seller.getBusinessHours().stream()
                .filter(bh -> bh.getDayOfWeek().equals("SUNDAY"))
                .findFirst().orElseThrow();
        Assertions.assertEquals(ConstantsMocks.EXPECTED_OPEN_AT_2, updatedSunday.getOpenAt());
        Assertions.assertEquals(ConstantsMocks.EXPECTED_CLOSE_AT_2, updatedSunday.getCloseAt());

        BusinessHour newMonday = seller.getBusinessHours().stream()
                .filter(bh -> bh.getDayOfWeek().equals("MONDAY"))
                .findFirst().orElseThrow();
        Assertions.assertEquals(ConstantsMocks.EXPECTED_OPEN_AT_3, newMonday.getOpenAt());
        Assertions.assertEquals(ConstantsMocks.EXPECTED_CLOSE_AT_3, newMonday.getCloseAt());

        Mockito.verify(sellerRepository, Mockito.times(1)).findByCode(seller.getCode());
        Mockito.verify(sellerRepository, Mockito.times(1)).save(seller);
    }

    @Test
    void shouldSuccessfullyPatchSellerStatusAndBusinessHours() {

        //Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);

        Mockito.when(sellerRepository.findByCode(seller.getCode())).thenReturn(Optional.of(seller));
        Mockito.when(contextHolder.getProfile()).thenReturn(Profile.ADMIN.name());
        Mockito.when(sellerRepository.save(Mockito.any(Seller.class)))
                .thenAnswer(invocation -> {
                    Seller saved = invocation.getArgument(0);
                    saved.getAudit().setUpdatedAt(LocalDateTime.now());
                    return saved;
                });

        List<BusinessHourDTORequest> businessHoursList = List.of(
                BusinessHourDTORequest.builder()
                        .dayOfWeek("SUNDAY")
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_2)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_2).build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek("MONDAY")
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_3)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_3).build());

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(Status.ACTIVE)
                .businessHours(businessHoursList)
                .build();

        //Act
        SellerUpdateDTOResponse response = sellerService.update(seller.getCode(), request);

        //Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(seller.getCode(), response.code());
        Assertions.assertEquals(Status.ACTIVE, response.status());
        Assertions.assertNotNull(response.updatedDate());
        Assertions.assertTrue(response.updatedDate().isAfter(seller.getAudit().getCreatedAt()));

        Assertions.assertEquals(2, seller.getBusinessHours().size());

        BusinessHour updatedSunday = seller.getBusinessHours().stream()
                .filter(bh -> bh.getDayOfWeek().equals("SUNDAY"))
                .findFirst().orElseThrow();
        Assertions.assertEquals(ConstantsMocks.EXPECTED_OPEN_AT_2, updatedSunday.getOpenAt());
        Assertions.assertEquals(ConstantsMocks.EXPECTED_CLOSE_AT_2, updatedSunday.getCloseAt());

        BusinessHour updatedMonday = seller.getBusinessHours().stream()
                .filter(bh -> bh.getDayOfWeek().equals("MONDAY"))
                .findFirst().orElseThrow();
        Assertions.assertEquals(ConstantsMocks.EXPECTED_OPEN_AT_3, updatedMonday.getOpenAt());
        Assertions.assertEquals(ConstantsMocks.EXPECTED_CLOSE_AT_3, updatedMonday.getCloseAt());

        Mockito.verify(sellerRepository, Mockito.times(1)).findByCode(seller.getCode());
        Mockito.verify(contextHolder, Mockito.times(1)).getProfile();
        Mockito.verify(sellerRepository, Mockito.times(1)).save(seller);

    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenSellerIsNotFound() {

        //Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);

        Mockito.when(sellerRepository.findByCode(seller.getCode())).thenReturn(Optional.empty());

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(Status.ACTIVE)
                .build();

        //Act e Assert
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
                sellerService.update(seller.getCode(), request));

        Assertions.assertEquals(MessageErrorConstants.ERROR_SELLER_NOT_FOUND, exception.getMessage());
        Mockito.verify(sellerRepository, Mockito.never()).save(Mockito.any(Seller.class));

    }

    @Test
    void shouldThrowBusinessExceptionWhenRequestIsNull() {

        //Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);

        //Act e Assert
        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> sellerService.update(seller.getCode(), null)
        );

        Assertions.assertEquals(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED, exception.getMessage());
        Mockito.verify(sellerRepository, Mockito.never()).findByCode(seller.getCode());
        Mockito.verify(sellerRepository, Mockito.never()).save(Mockito.any(Seller.class));

    }

    @Test
    void shouldThrowBusinessExceptionWhenStatusAndBusinessHoursAreNull() {

        //Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(null)
                .businessHours(null)
                .build();

        //Act e Assert
        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> sellerService.update(seller.getCode(), request)
        );

        Assertions.assertEquals(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED, exception.getMessage());
        Mockito.verify(sellerRepository, Mockito.never()).findByCode(seller.getCode());
        Mockito.verify(sellerRepository, Mockito.never()).save(Mockito.any());

    }

    @Test
    void shouldThrowBusinessExceptionWhenStatusIsNullAndBusinessHoursIsEmpty() {

        //Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(null)
                .businessHours(Collections.emptyList())
                .build();

        //Act e Assert
        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> sellerService.update(seller.getCode(), request)
        );

        Assertions.assertEquals(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED, exception.getMessage());
        Mockito.verify(sellerRepository, Mockito.never()).findByCode(seller.getCode());
        Mockito.verify(sellerRepository, Mockito.never()).save(Mockito.any());

    }

}
