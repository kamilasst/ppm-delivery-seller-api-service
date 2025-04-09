package com.ppm.delivery.seller.api.service.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ppm.delivery.seller.api.service.PpmDeliverySellerApiServiceApplication;
import com.ppm.delivery.seller.api.service.api.constants.HeaderConstants;
import com.ppm.delivery.seller.api.service.api.domain.request.BusinessHourDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerUpdateDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerUpdateDTOResponse;
import com.ppm.delivery.seller.api.service.builder.SellerBuilder;
import com.ppm.delivery.seller.api.service.builder.SellerDTORequestBuilder;
import com.ppm.delivery.seller.api.service.domain.model.BusinessHour;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import com.ppm.delivery.seller.api.service.utils.ConstantsMocks;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = {PpmDeliverySellerApiServiceApplication.class})
class SellerServiceComponentTest extends AbstractComponentTest{

    @Test
    @DisplayName("Should successfully POST")
    void shouldSuccessfullyPostSellerToDatabase() throws Exception {

        // arrange
        SellerDTORequest sellerDTORequest = SellerDTORequestBuilder.createDefault();
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller sellerByRequest = SellerBuilder.create(countryCode, sellerDTORequest);

        // act
        var resultActions = mockMvc
                .perform(
                        post("/api/seller/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                                .content(objectMapper.writeValueAsString(sellerDTORequest)))
                .andExpect(status().isCreated());

        SellerDTOResponse sellerDTOResponse = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // assert
        Optional<Seller> optionalSellerDatabase = sellerRepository.findAll().stream().findFirst();

        assertTrue(optionalSellerDatabase.isPresent());

        sellerByRequest.setCode(sellerDTOResponse.code());
        assertEquals(sellerByRequest, optionalSellerDatabase.get());
    }

    @Test
    void shouldSuccessfullyPatchSellerStatus() throws Exception {

        //Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        sellerRepository.saveAndFlush(seller);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(Status.ACTIVE).build();

        //Act
        var resultActions = mockMvc
                .perform(
                        patch("/api/seller/patch/{code}", seller.getCode())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        //Assert
        SellerUpdateDTOResponse response = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsString(),
                new TypeReference<>() {});

        Optional<Seller> updatedSeller = sellerRepository.findByCode(seller.getCode());
        assertTrue(updatedSeller.isPresent());

        assertEquals(request.status(), response.status());
        assertEquals(request.status(), updatedSeller.get().getStatus());
    }

    @Test
    void shouldSuccessfullyPatchSellerBusinessHour() throws Exception {

        //Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        sellerRepository.saveAndFlush(seller);

        List<BusinessHourDTORequest> businessHoursList  = List.of(
                BusinessHourDTORequest.builder()
                        .dayOfWeek("SUNDAY")
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_2)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_3).build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek("MONDAY")
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_3)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_3).build());

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHoursList)
                .build();

        // Act
        var resultActions = mockMvc
                .perform(
                        patch("/api/seller/patch/{code}", seller.getCode())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Assert
        SellerUpdateDTOResponse response = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsString(),
                new TypeReference<>() {});

        Optional<Seller> updatedSeller = sellerRepository.findByCode(seller.getCode());
        assertTrue(updatedSeller.isPresent());

        assertEquals(seller.getStatus(), response.status());
        assertEquals(seller.getStatus(), updatedSeller.get().getStatus());

        BusinessHour updatedSunday = updatedSeller.get().getBusinessHours().stream()
                .filter(bh -> bh.getDayOfWeek().equals("SUNDAY"))
                .findFirst().orElseThrow();
        assertEquals(ConstantsMocks.EXPECTED_OPEN_AT_2, updatedSunday.getOpenAt());
        assertEquals(ConstantsMocks.EXPECTED_CLOSE_AT_3, updatedSunday.getCloseAt());

        BusinessHour updatedMonday = updatedSeller.get().getBusinessHours().stream()
                .filter(bh -> bh.getDayOfWeek().equals("MONDAY"))
                .findFirst().orElseThrow();
        assertEquals(ConstantsMocks.EXPECTED_OPEN_AT_3, updatedMonday.getOpenAt());
        assertEquals(ConstantsMocks.EXPECTED_CLOSE_AT_3, updatedMonday.getCloseAt());

    }

    @Test
    void shouldSuccessfullyPatchSellerStatusAndBusinessHour() throws Exception {

        //Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        sellerRepository.saveAndFlush(seller);

        List<BusinessHourDTORequest> businessHoursList  = List.of(
                BusinessHourDTORequest.builder()
                        .dayOfWeek("SUNDAY")
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_2)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_3).build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek("MONDAY")
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_3)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_3).build());

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHoursList)
                .status(Status.ACTIVE)
                .build();

        // Act
        var resultActions = mockMvc
                .perform(
                        patch("/api/seller/patch/{code}", seller.getCode())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Assert
        SellerUpdateDTOResponse response = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsString(),
                new TypeReference<>() {});

        Optional<Seller> updatedSeller = sellerRepository.findByCode(seller.getCode());
        assertTrue(updatedSeller.isPresent());

        assertEquals(request.status(), response.status());
        assertEquals(request.status(), updatedSeller.get().getStatus());

        BusinessHour updatedSunday = updatedSeller.get().getBusinessHours().stream()
                .filter(bh -> bh.getDayOfWeek().equals("SUNDAY"))
                .findFirst().orElseThrow();
        assertEquals(ConstantsMocks.EXPECTED_OPEN_AT_2, updatedSunday.getOpenAt());
        assertEquals(ConstantsMocks.EXPECTED_CLOSE_AT_3, updatedSunday.getCloseAt());

        BusinessHour updatedMonday = updatedSeller.get().getBusinessHours().stream()
                .filter(bh -> bh.getDayOfWeek().equals("MONDAY"))
                .findFirst().orElseThrow();
        assertEquals(ConstantsMocks.EXPECTED_OPEN_AT_3, updatedMonday.getOpenAt());
        assertEquals(ConstantsMocks.EXPECTED_CLOSE_AT_3, updatedMonday.getCloseAt());
    }

    @Test
    void shouldReturn404WhenSellerCodeIsNotFound() throws Exception {

        // Arrange
        String nonExistentCode = "123456";
        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(Status.ACTIVE).build();

        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", nonExistentCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())  // Espera um status HTTP 404 (Not Found)
                .andExpect(jsonPath("$.error").value("Seller not found"));  // Verifica a mensagem da exceção
    }

    @Test
    void shouldReturn400WhenRequestIsNull() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .content("")) // corpo vazio = request null
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(MessageErrorConstants.ERROR_STATUS_AND_BUSINESSHOUR_MUST_BE_PROVIDED));
    }

    @Test
    void shouldReturn400WhenStatusAndBusinessHoursAreNull() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(null)
                .businessHours(null)
                .build();

        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(MessageErrorConstants.ERROR_STATUS_AND_BUSINESSHOUR_MUST_BE_PROVIDED));
    }

    @Test
    void shouldReturn400WhenStatusIsNullAndBusinessHoursIsEmpty() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(null)
                .businessHours(List.of())
                .build();

        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(MessageErrorConstants.ERROR_STATUS_AND_BUSINESSHOUR_MUST_BE_PROVIDED));
    }

}
