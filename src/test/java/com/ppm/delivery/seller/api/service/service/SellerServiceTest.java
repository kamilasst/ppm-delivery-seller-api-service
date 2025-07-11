package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.api.domain.request.BusinessHourDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerNearSearchRequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerUpdateDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerAvailableNearbyDTOResponse;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerUpdateDTOResponse;
import com.ppm.delivery.seller.api.service.api.interceptor.ContextHolder;
import com.ppm.delivery.seller.api.service.builder.SellerAvailableNearbyDTOResponseBuilder;
import com.ppm.delivery.seller.api.service.builder.SellerBuilder;
import com.ppm.delivery.seller.api.service.builder.SellerDTORequestBuilder;
import com.ppm.delivery.seller.api.service.constants.ConstantsMocks;
import com.ppm.delivery.seller.api.service.domain.mapper.SellerMapper;
import com.ppm.delivery.seller.api.service.domain.model.BusinessHour;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import com.ppm.delivery.seller.api.service.domain.profile.Profile;
import com.ppm.delivery.seller.api.service.exception.BusinessException;
import com.ppm.delivery.seller.api.service.exception.EntityNotFoundException;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import com.ppm.delivery.seller.api.service.repository.ISellerRepository;
import com.ppm.delivery.seller.api.service.utils.DateFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class SellerServiceTest {

    @InjectMocks
    private SellerService sellerService;

    @Mock
    private ISellerRepository sellerRepository;

    @Mock
    private ContextHolder contextHolder;

    @Mock
    private SellerMapper sellerMapper;

    @BeforeEach
    void setUp() {
        PermissionService permissionValidator = new PermissionService(contextHolder);
        sellerService = new SellerService(contextHolder, sellerRepository, permissionValidator, sellerMapper);
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
    void shouldSuccessfullyPatchSellerOnlyStatusWhenProfileIsAdmin() {

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
    void shouldThrowBusinessExceptionWhenPatchSellerStatusWhenProfileIsUser() {

        //Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);

        Mockito.when(sellerRepository.findByCode(seller.getCode())).thenReturn(Optional.of(seller));
        Mockito.when(contextHolder.getProfile()).thenReturn(Profile.USER.name());

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(Status.ACTIVE).build();

        //Act e Assert
        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> sellerService.update(seller.getCode(), request)
        );

        //Assert
        Assertions.assertEquals(MessageErrorConstants.ERROR_OPERATION_NOT_PERMITTED_FOR_THIS_PROFILE, exception.getMessage());
        Assertions.assertEquals(Status.PENDING, seller.getStatus());
        Mockito.verify(sellerRepository, Mockito.times(1)).findByCode(seller.getCode());
        Mockito.verify(contextHolder, Mockito.times(1)).getProfile();
        Mockito.verify(sellerRepository, Mockito.never()).save(Mockito.any(Seller.class));

    }

    @Test
    void shouldSuccessfullyPatchSellerOnlyBusinessHours() {

        //Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);
        seller.getBusinessHours().clear();

        Mockito.when(sellerRepository.findByCode(seller.getCode())).thenReturn(Optional.of(seller));
        Mockito.when(sellerRepository.save(Mockito.any(Seller.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<BusinessHourDTORequest> businessHoursList  = List.of(
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.SUNDAY)
                        .openAt(ConstantsMocks.TIME_08h30m00)
                        .closeAt(ConstantsMocks.TIME_17h30m00).build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openAt(ConstantsMocks.TIME_09h00m00)
                        .closeAt(ConstantsMocks.TIME_18h00m00).build());

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
                .filter(bh -> DayOfWeek.SUNDAY.name().equals(bh.getDayOfWeek()))
                .findFirst().orElseThrow();
        Assertions.assertEquals(ConstantsMocks.TIME_08h30m00, updatedSunday.getOpenAt());
        Assertions.assertEquals(ConstantsMocks.TIME_17h30m00, updatedSunday.getCloseAt());

        BusinessHour newMonday = seller.getBusinessHours().stream()
                .filter(bh -> DayOfWeek.MONDAY.name().equals(bh.getDayOfWeek()))
                .findFirst().orElseThrow();
        Assertions.assertEquals(ConstantsMocks.TIME_09h00m00, newMonday.getOpenAt());
        Assertions.assertEquals(ConstantsMocks.TIME_18h00m00, newMonday.getCloseAt());

        Mockito.verify(sellerRepository, Mockito.times(1)).findByCode(seller.getCode());
        Mockito.verify(sellerRepository, Mockito.times(1)).save(seller);
    }

    @Test
    void shouldSuccessfullyPatchSellerStatusAndBusinessHoursWhenProfileIsAdmin() {
        // Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);
        seller.getBusinessHours().clear();

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
                        .dayOfWeek(DayOfWeek.SUNDAY)
                        .openAt(ConstantsMocks.TIME_08h30m00)
                        .closeAt(ConstantsMocks.TIME_17h30m00)
                        .build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openAt(ConstantsMocks.TIME_09h00m00)
                        .closeAt(ConstantsMocks.TIME_18h00m00)
                        .build()
        );

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(Status.ACTIVE)
                .businessHours(businessHoursList)
                .build();

        // Act
        SellerUpdateDTOResponse response = sellerService.update(seller.getCode(), request);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(seller.getCode(), response.code());
        Assertions.assertEquals(Status.ACTIVE, response.status());
        Assertions.assertNotNull(response.updatedDate());
        Assertions.assertTrue(response.updatedDate().isAfter(seller.getAudit().getCreatedAt()));
        Assertions.assertEquals(2, seller.getBusinessHours().size());

        BusinessHour sunday = seller.getBusinessHours().stream()
                .filter(bh -> DayOfWeek.SUNDAY.name().equals(bh.getDayOfWeek()))
                .findFirst().orElseThrow();
        Assertions.assertEquals(ConstantsMocks.TIME_08h30m00, sunday.getOpenAt());
        Assertions.assertEquals(ConstantsMocks.TIME_17h30m00, sunday.getCloseAt());

        BusinessHour monday = seller.getBusinessHours().stream()
                .filter(bh -> DayOfWeek.MONDAY.name().equals(bh.getDayOfWeek()))
                .findFirst().orElseThrow();
        Assertions.assertEquals(ConstantsMocks.TIME_09h00m00, monday.getOpenAt());
        Assertions.assertEquals(ConstantsMocks.TIME_18h00m00, monday.getCloseAt());

        Mockito.verify(sellerRepository, Mockito.times(1)).findByCode(seller.getCode());
        Mockito.verify(contextHolder, Mockito.times(1)).getProfile();
        Mockito.verify(sellerRepository, Mockito.times(1)).save(seller);
    }

    @Test
    void shouldCreateSellerSuccessfullyWhenBusinessHoursIsNullOnCreate() {

        // Arrange
        SellerDTORequest request = SellerDTORequestBuilder.createWithNullBusinessHours();

        Mockito.when(contextHolder.getCountry()).thenReturn(ConstantsMocks.COUNTRY_CODE_BR);
        Mockito.when(sellerRepository.existsByIdentificationCode(Mockito.anyString())).thenReturn(false);

        ArgumentCaptor<Seller> sellerCaptor = ArgumentCaptor.forClass(Seller.class);
        Mockito.when(sellerRepository.save(Mockito.any(Seller.class)))
                .thenAnswer(invocation -> {
                    Seller sellerToSave = invocation.getArgument(0);
                    sellerToSave.setCode(UUID.randomUUID().toString());
                    sellerToSave.getAudit().setCreatedAt(LocalDateTime.now());
                    return sellerToSave;
                });

        // Act
        SellerDTOResponse response = sellerService.create(request);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.code());
        Assertions.assertNotNull(response.createdDate());
        Assertions.assertEquals(Status.PENDING, response.status());
        Mockito.verify(sellerRepository, Mockito.times(1)).save(Mockito.any(Seller.class));
    }

    @Test
    void shouldCreateSellerSuccessfullyWhenBusinessHoursIsNotPassedOnCreate() {

        // Arrange
        SellerDTORequest request = SellerDTORequestBuilder.createWithoutBusinessHours();

        Mockito.when(contextHolder.getCountry()).thenReturn(ConstantsMocks.COUNTRY_CODE_BR);
        Mockito.when(sellerRepository.existsByIdentificationCode(Mockito.anyString())).thenReturn(false);

        ArgumentCaptor<Seller> sellerCaptor = ArgumentCaptor.forClass(Seller.class);
        Mockito.when(sellerRepository.save(Mockito.any(Seller.class)))
                .thenAnswer(invocation -> {
                    Seller sellerToSave = invocation.getArgument(0);
                    sellerToSave.setCode(UUID.randomUUID().toString());
                    sellerToSave.getAudit().setCreatedAt(LocalDateTime.now());
                    return sellerToSave;
                });

        // Act
        SellerDTOResponse response = sellerService.create(request);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.code());
        Assertions.assertNotNull(response.createdDate());
        Assertions.assertEquals(Status.PENDING, response.status());
        Mockito.verify(sellerRepository, Mockito.times(1)).save(Mockito.any(Seller.class));
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
    void shouldThrowBusinessExceptionWhenRequestIsNullOnUpdate() {

        //Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);

        //Act e Assert
        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> sellerService.update(seller.getCode(), null)
        );

        Assertions.assertEquals(MessageErrorConstants.ERROR_REQUEST_BODY_IS_REQUIRED, exception.getMessage());
        Mockito.verify(sellerRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(sellerRepository).findByCode(seller.getCode());
    }

    @Test
    void shouldThrowBusinessExceptionWhenBusinessHoursIsNullOnUpdate() {

        // Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);

        Mockito.when(sellerRepository.findByCode(seller.getCode()))
                .thenReturn(Optional.of(seller));

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(null)
                .build();

        //Act e Assert
        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> sellerService.update(seller.getCode(), request)
        );

        // Assert
        Assertions.assertEquals(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED, exception.getMessage());
    }

    @Test
    void shouldThrowBusinessExceptionWhenBusinessHoursNotProvidedOnUpdate() {

        // Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .build();

        Mockito.when(sellerRepository.findByCode(seller.getCode()))
                .thenReturn(Optional.of(seller));

        //Act e Assert
        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> sellerService.update(seller.getCode(), request)
        );

        // Assert
        Assertions.assertEquals(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED, exception.getMessage());
    }

    @Test
    void shouldThrowBusinessExceptionWhenStatusAndBusinessHoursAreNullOnUpdate() {

        //Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);
        Mockito.when(sellerRepository.findByCode(seller.getCode()))
                .thenReturn(Optional.of(seller));

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(null)
                .businessHours(null)
                .build();

        //Act e Assert
        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> sellerService.update(seller.getCode(), request)
        );

        Assertions.assertEquals(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED, exception.getMessage());
        Mockito.verify(sellerRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(sellerRepository).findByCode(seller.getCode());
    }

    @Test
    void shouldThrowBusinessExceptionWhenStatusIsNullAndBusinessHoursIsEmptyOnUpdate() {

        //Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);

        Mockito.when(sellerRepository.findByCode(seller.getCode())).thenReturn(Optional.of(seller));

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(null)
                .businessHours(Collections.emptyList())
                .build();

        //Act e Assert
        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> sellerService.update(seller.getCode(), request)
        );

        Assertions.assertEquals(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED, exception.getMessage());
        Mockito.verify(sellerRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(sellerRepository).findByCode(seller.getCode());

    }

    @Test
    void shouldThrowBusinessExceptionWhenStatusIsNullAndBusinessHoursAreNotProvidedOnUpdate() {

        //Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);

        Mockito.when(sellerRepository.findByCode(seller.getCode())).thenReturn(Optional.of(seller));

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .build();

        //Act e Assert
        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> sellerService.update(seller.getCode(), request)
        );

        Assertions.assertEquals(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED, exception.getMessage());
        Mockito.verify(sellerRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(sellerRepository).findByCode(seller.getCode());

    }

    @Test
    void shouldReturnListOfSellerAvailableNearbyDTOResponse() {
        // Arrange
        SellerNearSearchRequest.DeliveryInfoDTO deliveryInfo =
                new SellerNearSearchRequest.DeliveryInfoDTO(-23.0, -46.0);

        LocalDateTime orderCreateDate = LocalDateTime.of(2025, 7, 10, 14, 30);
        String orderHours = DateFormatter.getHours(orderCreateDate);

        SellerNearSearchRequest request =
                new SellerNearSearchRequest(orderCreateDate, deliveryInfo, 10.0, null);

        Mockito.when(contextHolder.getCountry()).thenReturn(ConstantsMocks.COUNTRY_CODE_BR);

        Seller seller1 = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);
        Seller seller2 = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);
        seller2.setCode("11111111-1111-1111-1111-111111111111");
        List<Seller> sellers = List.of(seller1, seller2);

        Mockito.when(sellerRepository.searchAvailableNearby(
                        Mockito.eq(ConstantsMocks.COUNTRY_CODE_BR),
                        Mockito.eq(deliveryInfo.latitude()),
                        Mockito.eq(deliveryInfo.longitude()),
                        Mockito.eq(request.radius()),
                        Mockito.eq(orderCreateDate.getDayOfWeek().name()),
                        Mockito.eq(orderHours)))
                .thenReturn(sellers);

        SellerAvailableNearbyDTOResponse dto1 = SellerAvailableNearbyDTOResponseBuilder.createDefault(seller1);
        SellerAvailableNearbyDTOResponse dto2 = SellerAvailableNearbyDTOResponseBuilder.createDefault(seller2);
        List<SellerAvailableNearbyDTOResponse> expectedDTOs = List.of(dto1, dto2);

        Mockito.when(sellerMapper.toSellerAvailableNearbyDTOList(sellers))
                .thenReturn(expectedDTOs);

        // Act
        List<SellerAvailableNearbyDTOResponse> result = sellerService.searchAvailableNearby(request);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());

        for (int i = 0; i < expectedDTOs.size(); i++) {
            SellerAvailableNearbyDTOResponse expected = expectedDTOs.get(i);
            SellerAvailableNearbyDTOResponse actual   = result.get(i);

            Assertions.assertAll("Verify DTO[" + i + "]",
                    () -> Assertions.assertEquals(expected.getCode(),        actual.getCode(),        "code"),
                    () -> Assertions.assertEquals(expected.getCountryCode(), actual.getCountryCode(), "countryCode"),
                    () -> Assertions.assertEquals(expected.getName(),        actual.getName(),        "name"),
                    () -> Assertions.assertEquals(expected.getDisplayName(), actual.getDisplayName(), "displayName"),
                    () -> Assertions.assertEquals(expected.getStatus(),      actual.getStatus(),      "status"),

                    () -> Assertions.assertEquals(expected.getIdentification().getType(),
                            actual.getIdentification().getType(), "ident.type"),
                    () -> Assertions.assertEquals(expected.getIdentification().getCode(),
                            actual.getIdentification().getCode(), "ident.code"),

                    () -> {
                        Assertions.assertEquals(expected.getContacts().size(),
                                actual.getContacts().size(), "contacts.size");
                        var expContact = expected.getContacts().get(0);
                        var actContact = actual  .getContacts().get(0);
                        Assertions.assertEquals(expContact.getType(),  actContact.getType(),  "contact.type");
                        Assertions.assertEquals(expContact.getValue(), actContact.getValue(), "contact.value");
                    },

                    () -> {
                        var expLoc = expected.getAddress().location();
                        var actLoc = actual  .getAddress().location();
                        Assertions.assertEquals(expLoc.city(),          actLoc.city(),          "city");
                        Assertions.assertEquals(expLoc.country(),       actLoc.country(),       "country");
                        Assertions.assertEquals(expLoc.state(),         actLoc.state(),         "state");
                        Assertions.assertEquals(expLoc.number(),        actLoc.number(),        "number");
                        Assertions.assertEquals(expLoc.zipCode(),       actLoc.zipCode(),       "zipCode");
                        Assertions.assertEquals(expLoc.streetAddress(), actLoc.streetAddress(), "streetAddress");
                        Assertions.assertEquals(expLoc.geoCoordinates().latitude(),
                                actLoc.geoCoordinates().latitude(),  "lat");
                        Assertions.assertEquals(expLoc.geoCoordinates().longitude(),
                                actLoc.geoCoordinates().longitude(), "lon");
                    },

                    () -> {
                        Assertions.assertEquals(expected.getBusinessHours().size(),
                                actual.getBusinessHours().size(), "hours.size");
                        for (int j = 0; j < expected.getBusinessHours().size(); j++) {
                            var expH = expected.getBusinessHours().get(j);
                            var actH = actual.getBusinessHours().get(j);
                            Assertions.assertEquals(expH.getDayOfWeek(), expH.getDayOfWeek(),
                                    "hours.dayOfWeek[" + j + "]");
                            Assertions.assertEquals(expH.getOpenAt(),    actH.getOpenAt(),
                                    "hours.openAt[" + j + "]");
                            Assertions.assertEquals(expH.getCloseAt(),   actH.getCloseAt(),
                                    "hours.closeAt[" + j + "]");
                        }
                    }
            );
        }

        Mockito.verify(contextHolder).getCountry();
        Mockito.verify(sellerRepository, Mockito.times(1)).searchAvailableNearby(
                ConstantsMocks.COUNTRY_CODE_BR,
                deliveryInfo.latitude(),
                deliveryInfo.longitude(),
                request.radius(),
                orderCreateDate.getDayOfWeek().name(),
                orderHours
        );
        Mockito.verify(sellerMapper).toSellerAvailableNearbyDTOList(sellers);
    }

    @Test
    void shouldReturnEmptyListWhenNoSellerFound() {
        // Arrange
        SellerNearSearchRequest.DeliveryInfoDTO deliveryInfo =
                new SellerNearSearchRequest.DeliveryInfoDTO(-23.0, -46.0);

        LocalDateTime orderCreateDate = LocalDateTime.of(2025, 7, 10, 14, 30);
        String orderHours = DateFormatter.getHours(orderCreateDate);

        SellerNearSearchRequest request =
                new SellerNearSearchRequest(orderCreateDate, deliveryInfo, 10.0, null);

        Mockito.when(contextHolder.getCountry()).thenReturn(ConstantsMocks.COUNTRY_CODE_BR);

        Mockito.when(sellerRepository.searchAvailableNearby(
                        Mockito.eq(ConstantsMocks.COUNTRY_CODE_BR),
                        Mockito.eq(deliveryInfo.latitude()),
                        Mockito.eq(deliveryInfo.longitude()),
                        Mockito.eq(10.0),
                        Mockito.eq(orderCreateDate.getDayOfWeek().name()),
                        Mockito.eq(orderHours)))
                .thenReturn(new ArrayList<>());

        Mockito.when(sellerMapper.toSellerAvailableNearbyDTOList(new ArrayList<>())).thenReturn(new ArrayList<>());

        // Act
        List<SellerAvailableNearbyDTOResponse> result = sellerService.searchAvailableNearby(request);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());

        Mockito.verify(contextHolder).getCountry();
        Mockito.verify(sellerRepository, Mockito.times(1)).searchAvailableNearby(
                Mockito.eq(ConstantsMocks.COUNTRY_CODE_BR),
                Mockito.eq(deliveryInfo.latitude()),
                Mockito.eq(deliveryInfo.longitude()),
                Mockito.eq(request.radius()),
                Mockito.eq(orderCreateDate.getDayOfWeek().name()),
                Mockito.eq(orderHours));
        Mockito.verify(sellerMapper).toSellerAvailableNearbyDTOList(new ArrayList<>());
    }
}
