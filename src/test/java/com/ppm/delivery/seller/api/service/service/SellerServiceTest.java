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
import com.ppm.delivery.seller.api.service.exception.BusinessException;
import com.ppm.delivery.seller.api.service.exception.EntityNotFoundException;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
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

import java.time.LocalDateTime;
import java.util.*;

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

        SellerDTOResponse response = sellerService.create(request);

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
    void shouldThrowExceptionWhenIdentificationCodeAlreadyExists(){

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.create(countryCode, request);

        Mockito.when(contextHolder.getCountry()).thenReturn(countryCode);
        Mockito.when(sellerRepository.existsByIdentificationCode(seller.getIdentification().getCode())).thenReturn(true);

        Assertions.assertThrows(BusinessException.class, () -> sellerService.create(request));

        Mockito.verify(sellerRepository, Mockito.never()).save(Mockito.any(Seller.class));
    }

    @Test
    void shouldUpdateOnlySellerStatusSuccessfully() {

        //Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);

        Mockito.when(sellerRepository.findByCode(seller.getCode())).thenReturn(Optional.of(seller));
        Mockito.when(sellerRepository.save(Mockito.any(Seller.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SellerUpdateDTORequest sellerUpdateDTORequest = SellerUpdateDTORequest.builder()
                .status(Status.ACTIVE).build();

        //Act
        SellerUpdateDTOResponse response = sellerService.update(seller.getCode(), sellerUpdateDTORequest);

        //Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(seller.getCode(), response.code());
        Assertions.assertEquals(Status.ACTIVE, response.status());
        Assertions.assertNotNull(response.updatedDate());
        Assertions.assertTrue(response.updatedDate().isAfter(seller.getAudit().getCreatedAt()));

        Mockito.verify(sellerRepository, Mockito.times(1)).findByCode(seller.getCode());
        Mockito.verify(sellerRepository, Mockito.times(1)).save(seller);
    }

    @Test
    void shouldUpdateOnlySellerBusinessHoursSuccessfully() {

        //Arrange
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);
        seller.setStatus(Status.ACTIVE);

        Mockito.when(sellerRepository.findByCode(seller.getCode())).thenReturn(Optional.of(seller));
        Mockito.when(sellerRepository.save(Mockito.any(Seller.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<BusinessHourDTORequest> mutableList = List.of(
                BusinessHourDTORequest.builder()
                        .dayOfWeek("SUNDAY")
                        .openAt("08:30:00")
                        .closeAt("17:30:00").build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek("MONDAY")
                        .openAt("09:00:00")
                        .closeAt("18:00:00").build());

        SellerUpdateDTORequest sellerUpdateDTORequest = SellerUpdateDTORequest.builder()
                .businessHours(mutableList)
                .build();


        //Act
        SellerUpdateDTOResponse response = sellerService.update(seller.getCode(), sellerUpdateDTORequest);

        //Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(seller.getCode(), response.code());
        Assertions.assertEquals(Status.ACTIVE, response.status());
        Assertions.assertNotNull(response.updatedDate());
        Assertions.assertTrue(response.updatedDate().isAfter(seller.getAudit().getCreatedAt()));

        Assertions.assertEquals(2, seller.getBusinessHours().size());

        BusinessHour updatedMonday = seller.getBusinessHours().stream()
                .filter(bh -> bh.getDayOfWeek().equals("SUNDAY"))
                .findFirst().orElseThrow();
        Assertions.assertEquals("08:30:00", updatedMonday.getOpenAt());
        Assertions.assertEquals("17:30:00", updatedMonday.getCloseAt());

        BusinessHour newTuesday = seller.getBusinessHours().stream()
                .filter(bh -> bh.getDayOfWeek().equals("MONDAY"))
                .findFirst().orElseThrow();
        Assertions.assertEquals("09:00:00", newTuesday.getOpenAt());
        Assertions.assertEquals("18:00:00", newTuesday.getCloseAt());

        Mockito.verify(sellerRepository, Mockito.times(1)).findByCode(seller.getCode());
        Mockito.verify(sellerRepository, Mockito.times(1)).save(seller);
    }

//    @Test
//    void shouldUpdateSellerStatusAndBusinessHoursSuccessfully() {
//        String sellerCode = "1234";
//        LocalDateTime beforeUpdate = LocalDateTime.now().minusDays(1);
//
//        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
//        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
//
//        Seller seller = SellerBuilder.create(countryCode, request);
//        seller.setCode(sellerCode);
//        seller.setStatus(Status.PENDING);
//        seller.setAudit(new Audit(beforeUpdate, null));
//
//        List<BusinessHourDTORequest> newBusinessHoursDTO = List.of(
//                new BusinessHourDTORequest("SUNDAY", "08:30:00", "17:30:00"),
//                new BusinessHourDTORequest("MONDAY", "09:00:00", "18:00:00")
//        );
//
//        // Atualizando o status e os business hours
//        SellerUpdateDTORequest sellerUpdateDTORequest = new SellerUpdateDTORequest(
//                Optional.of(Status.ACTIVE),
//                Optional.of(newBusinessHoursDTO)
//        );
//
//        Mockito.when(sellerRepository.findByCode(sellerCode)).thenReturn(Optional.of(seller));
//        Mockito.when(sellerRepository.save(Mockito.any(Seller.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        SellerUpdateDTOResponse response = sellerService.update(sellerCode, sellerUpdateDTORequest);
//
//        Assertions.assertNotNull(response);
//        Assertions.assertEquals(sellerCode, response.code());
//        Assertions.assertEquals(Status.ACTIVE, response.status());  // O status deve ter sido alterado para ACTIVE
//        Assertions.assertNotNull(response.updatedDate());
//        Assertions.assertTrue(response.updatedDate().isAfter(beforeUpdate));  // A data de atualização deve ser após a data original
//
//        // Verifica se os horários de funcionamento foram atualizados corretamente
//        Assertions.assertEquals(2, seller.getBusinessHours().size());  // Verifica que dois horários de funcionamento foram atualizados
//
//        // Verificando os horários de funcionamento atualizados para o domingo
//        BusinessHour updatedSunday = seller.getBusinessHours().stream()
//                .filter(bh -> bh.getDayOfWeek().equals("SUNDAY"))
//                .findFirst().orElseThrow();
//        Assertions.assertEquals("08:30:00", updatedSunday.getOpenAt());
//        Assertions.assertEquals("17:30:00", updatedSunday.getCloseAt());
//
//        // Verificando os horários de funcionamento atualizados para a segunda-feira
//        BusinessHour updatedMonday = seller.getBusinessHours().stream()
//                .filter(bh -> bh.getDayOfWeek().equals("MONDAY"))
//                .findFirst().orElseThrow();
//        Assertions.assertEquals("09:00:00", updatedMonday.getOpenAt());
//        Assertions.assertEquals("18:00:00", updatedMonday.getCloseAt());
//
//        Mockito.verify(sellerRepository, Mockito.times(1)).save(seller);  // Verifica se o save foi chamado uma vez
//    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenSellerIsNotFound() {
        Seller seller = SellerBuilder.createDefault(ConstantsMocks.COUNTRY_CODE_BR);

        Mockito.when(sellerRepository.findByCode(seller.getCode())).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
            sellerService.update(seller.getCode(), new SellerUpdateDTORequest(null, Collections.emptyList())));

        Assertions.assertEquals(MessageErrorConstants.ERROR_SELLER_NOT_FOUND, exception.getMessage());
    }

}
