package com.ppm.delivery.seller.api.service.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ppm.delivery.seller.api.service.PpmDeliverySellerApiServiceApplication;
import com.ppm.delivery.seller.api.service.api.constants.HeaderConstants;
import com.ppm.delivery.seller.api.service.api.domain.request.*;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerUpdateDTOResponse;
import com.ppm.delivery.seller.api.service.builder.SellerBuilder;
import com.ppm.delivery.seller.api.service.builder.SellerDTORequestBuilder;
import com.ppm.delivery.seller.api.service.builder.SellerInvalidJsonSamples;
import com.ppm.delivery.seller.api.service.domain.model.BusinessHour;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import com.ppm.delivery.seller.api.service.domain.profile.Profile;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import com.ppm.delivery.seller.api.service.utils.ConstantsMocks;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = {PpmDeliverySellerApiServiceApplication.class})
class SellerServiceComponentTest extends AbstractComponentTest {

    @Test
    @DisplayName("Should successfully POST With Profile is User")
    void shouldSuccessfullyPostSellerToDatabaseWithProfileIsUser() throws Exception {

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
                                .header(HeaderConstants.HEADER_PROFILE, Profile.USER.name())
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
    @DisplayName("Should successfully POST With Profile is ADMIN")
    void shouldSuccessfullyPostSellerToDatabaseWithProfileIsAdmin() throws Exception {

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
                                .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
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
    void shouldSuccessfullyPatchSellerOnlyStatusWhenProfileIsAdmin() throws Exception {

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
                                .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        //Assert
        SellerUpdateDTOResponse response = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        Optional<Seller> updatedSeller = sellerRepository.findByCode(seller.getCode());
        assertTrue(updatedSeller.isPresent());

        assertEquals(request.status(), response.status());
        assertEquals(request.status(), updatedSeller.get().getStatus());
    }

    @Test
    void shouldReturnBadRequestPatchSellerOnlyStatusWhenProfileIsUser() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        sellerRepository.saveAndFlush(seller);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(Status.ACTIVE)
                .build();

        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.USER.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(MessageErrorConstants.ERROR_OPERATION_NOT_PERMITTED_FOR_THIS_PROFILE));
    }

    @Test
    void shouldReturnBadRequestWhenPatchStatusProfileHeaderIsBlank() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        sellerRepository.saveAndFlush(seller);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(Status.ACTIVE)
                .build();

        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, " ")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(MessageErrorConstants.ERROR_PROFILE_REQUIRED_HEADER));
    }

    @Test
    void shouldReturnBadRequestWhenPatchStatusWithProfileHeaderIsNotValidEnum() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        sellerRepository.saveAndFlush(seller);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(Status.ACTIVE)
                .build();

        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, "INVALID_PROFILE")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(MessageErrorConstants.ERROR_PROFILE_IS_INVALID));
    }

    @Test
    void shouldSuccessfullyPatchSellerOnlyBusinessHoursWhenProfileIsAdmin() throws Exception {

        //Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);
        seller.getBusinessHours().clear();

        sellerRepository.saveAndFlush(seller);

        List<BusinessHourDTORequest> businessHoursList = List.of(
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.SUNDAY)
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_2)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_3).build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
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
                                .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Assert
        SellerUpdateDTOResponse response = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        Optional<Seller> updatedSeller = sellerRepository.findByCode(seller.getCode());
        assertTrue(updatedSeller.isPresent());

        assertEquals(seller.getStatus(), response.status());
        assertEquals(seller.getStatus(), updatedSeller.get().getStatus());

        BusinessHour updatedSunday = updatedSeller.get().getBusinessHours().stream()
                .filter(bh -> DayOfWeek.SUNDAY.name().equals(bh.getDayOfWeek()))
                .findFirst().orElseThrow();
        assertEquals(ConstantsMocks.EXPECTED_OPEN_AT_2, updatedSunday.getOpenAt());
        assertEquals(ConstantsMocks.EXPECTED_CLOSE_AT_3, updatedSunday.getCloseAt());

        BusinessHour updatedMonday = updatedSeller.get().getBusinessHours().stream()
                .filter(bh -> DayOfWeek.MONDAY.name().equals(bh.getDayOfWeek()))
                .findFirst().orElseThrow();
        assertEquals(ConstantsMocks.EXPECTED_OPEN_AT_3, updatedMonday.getOpenAt());
        assertEquals(ConstantsMocks.EXPECTED_CLOSE_AT_3, updatedMonday.getCloseAt());
    }

    @Test
    void shouldSuccessfullyPatchSellerOnlyBusinessHoursWhenProfileIsUser() throws Exception {

        //Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);
        seller.getBusinessHours().clear();

        sellerRepository.saveAndFlush(seller);

        List<BusinessHourDTORequest> businessHoursList = List.of(
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.SUNDAY)
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_2)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_3).build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
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
                                .header(HeaderConstants.HEADER_PROFILE, Profile.USER.name())
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Assert
        SellerUpdateDTOResponse response = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        Optional<Seller> updatedSeller = sellerRepository.findByCode(seller.getCode());
        assertTrue(updatedSeller.isPresent());

        assertEquals(seller.getStatus(), response.status());
        assertEquals(seller.getStatus(), updatedSeller.get().getStatus());

        BusinessHour updatedSunday = updatedSeller.get().getBusinessHours().stream()
                .filter(bh -> DayOfWeek.SUNDAY.name().equals(bh.getDayOfWeek()))
                .findFirst().orElseThrow();
        assertEquals(ConstantsMocks.EXPECTED_OPEN_AT_2, updatedSunday.getOpenAt());
        assertEquals(ConstantsMocks.EXPECTED_CLOSE_AT_3, updatedSunday.getCloseAt());

        BusinessHour updatedMonday = updatedSeller.get().getBusinessHours().stream()
                .filter(bh -> DayOfWeek.MONDAY.name().equals(bh.getDayOfWeek()))
                .findFirst().orElseThrow();
        assertEquals(ConstantsMocks.EXPECTED_OPEN_AT_3, updatedMonday.getOpenAt());
        assertEquals(ConstantsMocks.EXPECTED_CLOSE_AT_3, updatedMonday.getCloseAt());
    }

    @Test
    void shouldSuccessfullyPatchSellerStatusAndBusinessHoursWhenProfileIsAdmin() throws Exception {

        //Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);
        seller.getBusinessHours().clear();

        sellerRepository.saveAndFlush(seller);

        List<BusinessHourDTORequest> businessHoursList = List.of(
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.SUNDAY)
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_2)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_3).build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
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
                                .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Assert
        SellerUpdateDTOResponse response = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        Optional<Seller> updatedSeller = sellerRepository.findByCode(seller.getCode());
        assertTrue(updatedSeller.isPresent());

        assertEquals(request.status(), response.status());
        assertEquals(request.status(), updatedSeller.get().getStatus());

        BusinessHour updatedSunday = updatedSeller.get().getBusinessHours().stream()
                .filter(bh -> DayOfWeek.SUNDAY.name().equals(bh.getDayOfWeek()))
                .findFirst().orElseThrow();
        assertEquals(ConstantsMocks.EXPECTED_OPEN_AT_2, updatedSunday.getOpenAt());
        assertEquals(ConstantsMocks.EXPECTED_CLOSE_AT_3, updatedSunday.getCloseAt());

        BusinessHour updatedMonday = updatedSeller.get().getBusinessHours().stream()
                .filter(bh -> DayOfWeek.MONDAY.name().equals(bh.getDayOfWeek()))
                .findFirst().orElseThrow();
        assertEquals(ConstantsMocks.EXPECTED_OPEN_AT_3, updatedMonday.getOpenAt());
        assertEquals(ConstantsMocks.EXPECTED_CLOSE_AT_3, updatedMonday.getCloseAt());
    }

    @Test
    void shouldReturnBadRequestWhenPatchBusinessHoursProfileIsBlank() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        sellerRepository.saveAndFlush(seller);

        List<BusinessHourDTORequest> businessHoursList = List.of(
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.SUNDAY)
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_2)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_3).build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_3)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_3).build());

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHoursList)
                .build();

        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, " ")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(MessageErrorConstants.ERROR_PROFILE_REQUIRED_HEADER));
    }

    @Test
    void shouldReturnBadRequestWhenPatchBusinessHoursWithProfileHeaderIsNotValidEnum() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        sellerRepository.saveAndFlush(seller);

        List<BusinessHourDTORequest> businessHoursList = List.of(
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.SUNDAY)
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_2)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_3).build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openAt(ConstantsMocks.EXPECTED_OPEN_AT_3)
                        .closeAt(ConstantsMocks.EXPECTED_CLOSE_AT_3).build());

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHoursList)
                .build();

        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, "INVALID_PROFILE")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(MessageErrorConstants.ERROR_PROFILE_IS_INVALID));
    }

    @Test
    void shouldReturnBadRequestWhenStatusAndBusinessHoursAreNull() throws Exception {

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
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED));
    }

    @Test
    void shouldReturnNotFoundWhenSellerCodeIsNotFound() throws Exception {

        // Arrange
        String nonExistentCode = "123456";
        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(Status.ACTIVE).build();

        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", nonExistentCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(MessageErrorConstants.ERROR_SELLER_NOT_FOUND));
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsNullOnPatch() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(MessageErrorConstants.ERROR_REQUEST_BODY_IS_REQUIRED));
    }

    @Test
    void shouldSuccessfullyPostWhenBusinessHoursIsNullOnPost() throws Exception {

        // arrange
        SellerDTORequest request = SellerDTORequestBuilder.createWithNullBusinessHours();
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller expectedSeller = SellerBuilder.createAllowingNullBusinessHours(countryCode, request);

        // act
        var resultActions = mockMvc
                .perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, countryCode)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        SellerDTOResponse response = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // assert
        Optional<Seller> savedSeller = sellerRepository.findAll().stream().findFirst();
        assertTrue(savedSeller.isPresent());

        expectedSeller.setCode(response.code());
        assertEquals(expectedSeller, savedSeller.get());
    }

    @Test
    void shouldSuccessfullyPostWhenBusinessHoursIsNotProvidedOnPost() throws Exception {

        // arrange
        SellerDTORequest request = SellerDTORequestBuilder.createWithoutBusinessHours();
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller expectedSeller = SellerBuilder.createWithoutBusinessHours(countryCode, request);

        // act
        var resultActions = mockMvc
                .perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, countryCode)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        SellerDTOResponse response = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // assert
        Optional<Seller> savedSeller = sellerRepository.findAll().stream().findFirst();
        assertTrue(savedSeller.isPresent());

        expectedSeller.setCode(response.code());
        assertEquals(expectedSeller, savedSeller.get());
    }

    @Test
    void shouldReturnBadRequestWhenBusinessHoursIsEmptyOnPost() throws Exception {

        // Arrange
        SellerDTORequest request = SellerDTORequestBuilder.createWithEmptyBusinessHours();


        // Act & Assert
        mockMvc.perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("At least one business hour must be provided.")));
    }

    @Test
    void shouldReturnBadRequestWhenBusinessHoursFieldHasInvalidJsonOnPost() throws Exception {

        // Arrange
        String malformedJson = SellerInvalidJsonSamples.businessHoursMissingValue();

        // Act & Assert
        mockMvc.perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error JSON Inválido - JSON parse error: Unexpected character ('}' (code 125)): expected a value")));
    }

    @Test
    void shouldReturnBadRequestWhenDayOfWeekIsNullOnPost() throws Exception {
        // Arrange
        BusinessHourDTORequest businessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(null)
                .openAt("08:00:00")
                .closeAt("18:00:00")
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);

        // Act & Assert
        mockMvc.perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Day of week must be provided.")));
    }

    @Test
    void shouldReturnBadRequestWhenDayOfWeekIsInvalidOnPost() throws Exception {

        String invalidRequest = """
                    {
                        "businessHours": [
                            {
                                "dayOfWeek": "abcde",
                                "openAt": "08:00:00",
                                "closeAt": "18:00:00"
                            }
                        ]
                    }
                """;

        mockMvc.perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Error JSON Inválido - JSON parse error: Cannot deserialize value of type `java.time.DayOfWeek` from String")));
    }

    @Test
    void shouldReturnBadRequestWhenDayOfWeekIsBlankOnPost() throws Exception {

        String requestWithBlankDayOfWeek = """
                    {
                        "businessHours": [
                            {
                                "dayOfWeek": " ",
                                "openAt": "08:00:00",
                                "closeAt": "18:00:00"
                            }
                        ]
                    }
                """;

        mockMvc.perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(requestWithBlankDayOfWeek))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Error JSON Inválido - JSON parse error: Cannot coerce empty String (\\\"\\\") to `java.time.DayOfWeek`")));
    }

    @Test
    void shouldReturnBadRequestWhenDayOfWeekIsNotProvidedOnPost() throws Exception {
        // Arrange
        BusinessHourDTORequest businessHour = BusinessHourDTORequest.builder()
                .openAt("08:00:00")
                .closeAt("18:00:00")
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);

        // Act & Assert
        mockMvc.perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Day of week must be provided.")));
    }

    @Test
    void shouldReturnBadRequestWhenOpenTimeIsInvalidOnPost() throws Exception {
        // Arrange
        BusinessHourDTORequest businessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("08:00")
                .closeAt("18:00:00")
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);


        // Act & Assert
        mockMvc.perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("businessHours[0].openAt: Invalid opening time. Times must be in 24-hour format, i.e., HH:mm:ss (e.g., 08:00:00 or 23:59:00)")));
    }

    @Test
    void shouldReturnBadRequestWhenOpenTimeIsNullOnPost() throws Exception {
        // Arrange
        BusinessHourDTORequest businessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt(null)
                .closeAt("18:00:00")
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);


        // Act & Assert
        mockMvc.perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Open time must be provided.")));
    }

    @Test
    void shouldReturnBadRequestWhenOpenTimeIsNotProvidesOnPost() throws Exception {
        // Arrange
        BusinessHourDTORequest businessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .closeAt("18:00:00")
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);


        // Act & Assert
        mockMvc.perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Open time must be provided.")));
    }

    @Test
    void shouldReturnBadRequestWhenCloseTimeIsInvalidOnPost() throws Exception {
        // Arrange
        BusinessHourDTORequest businessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("08:00:00")
                .closeAt("24:00:00")
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);


        // Act & Assert
        mockMvc.perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("businessHours[0].closeAt: Invalid closing time. Times must be in 24-hour format, i.e., HH:mm:ss (e.g., 08:00:00 or 23:59:00)")));
    }

    @Test
    void shouldReturnBadRequestWhenCloseTimeIsNullOnPost() throws Exception {
        // Arrange
        BusinessHourDTORequest businessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("08:00:00")
                .closeAt(null)
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);


        // Act & Assert
        mockMvc.perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Close time must be provided.")));
    }

    @Test
    void shouldReturnBadRequestWhenCloseTimeIsNotProvidedOnPost() throws Exception {
        // Arrange
        BusinessHourDTORequest businessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("08:00:00")
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);


        // Act & Assert
        mockMvc.perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Close time must be provided.")));
    }

    @Test
    void shouldReturnBadRequestWhenOpenAndCloseTimeAreInvalidOnPost() throws Exception {
        // Arrange
        BusinessHourDTORequest businessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("08:00")
                .closeAt("24:00:00")
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);


        // Act & Assert
        mockMvc.perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("businessHours[0].closeAt: Invalid closing time. Times must be in 24-hour format, i.e., HH:mm:ss (e.g., 08:00:00 or 23:59:00)")))
                .andExpect(content().string(containsString("businessHours[0].openAt: Invalid opening time. Times must be in 24-hour format, i.e., HH:mm:ss (e.g., 08:00:00 or 23:59:00)")));
    }

    @Test
    void shouldReturnBadRequestWhenOpenAndCloseTimeAreNullsOnPost() throws Exception {
        // Arrange
        BusinessHourDTORequest businessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt(null)
                .closeAt(null)
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);


        // Act & Assert
        mockMvc.perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Open time must be provided.")))
                .andExpect(content().string(containsString("Close time must be provided.")));
    }

    @Test
    void shouldReturnBadRequestWhenOpenAndCloseTimeAreNotProvidedOnPost() throws Exception {
        // Arrange
        BusinessHourDTORequest businessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);


        // Act & Assert
        mockMvc.perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Open time must be provided.")))
                .andExpect(content().string(containsString("Close time must be provided.")));
    }

    @Test
    void shouldReturnBadRequestWhenBusinessHoursIsNullOnPatch() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(null)
                .build();

        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED));
    }

    @Test
    void shouldReturnBadRequestWhenBusinessHoursIsNotProvidedOnPatch() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .build();

        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED));
    }

    @Test
    void shouldReturnBadRequestWhenBusinessHoursIsEmptyOnPatch() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(Collections.emptyList())
                .build();

        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("At least one business hour must be provided.")));
    }

    @Test
    void shouldReturnBadRequestWhenBusinessHoursFieldHasInvalidJsonOnPatch() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        String invalidJson = """
                {
                    "businessHours":
                }
                """;

        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error JSON Inválido - JSON parse error: Unexpected character ('}' (code 125)): expected a value")));
    }

    @Test
    void shouldReturnBadRequestWhenDayOfWeekIsNullOnPatch() throws Exception {
        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek(null)
                .openAt("08:00:00")
                .closeAt("18:00:00")
                .build();

        List<BusinessHourDTORequest> businessHours = new ArrayList<>();
        businessHours.add(dto);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHours)
                .build();
        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Day of week must be provided.")));
    }

    @Test
    void shouldReturnBadRequestWhenDayOfWeekIsNotProvidedOnPatch() throws Exception {
        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .openAt("08:00:00")
                .closeAt("18:00:00")
                .build();

        List<BusinessHourDTORequest> businessHours = new ArrayList<>();
        businessHours.add(dto);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHours)
                .build();
        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Day of week must be provided.")));
    }


    @Test
    void shouldReturnBadRequestWhenDayOfWeekIsInvalidOnPatch() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        String invalidRequest = """
                    {
                        "businessHours": [
                            {
                                "dayOfWeek": "abcde",
                                "openAt": "08:00:00",
                                "closeAt": "18:00:00"
                            }
                        ]
                    }
                """;

        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Error JSON Inválido - JSON parse error: Cannot deserialize value of type `java.time.DayOfWeek` from String")));
    }

    @Test
    void shouldReturnBadRequestWhenDayOfWeekIsBlankOnPatch() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        String requestWithBlankDayOfWeek = """
                    {
                        "businessHours": [
                            {
                                "dayOfWeek": " ",
                                "openAt": "08:00:00",
                                "closeAt": "18:00:00"
                            }
                        ]
                    }
                """;

        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(requestWithBlankDayOfWeek))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Error JSON Inválido - JSON parse error: Cannot coerce empty String (\\\"\\\") to `java.time.DayOfWeek`")));
    }

    @Test
    void shouldReturnBadRequestWhenOpenTimeIsInvalidOnPatch() throws Exception {
        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("08:00")
                .closeAt("18:00:00")
                .build();

        List<BusinessHourDTORequest> businessHours = new ArrayList<>();
        businessHours.add(dto);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHours)
                .build();
        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("businessHours[0].openAt: Invalid opening time. Times must be in 24-hour format, i.e., HH:mm:ss (e.g., 08:00:00 or 23:59:00)")));
    }

    @Test
    void shouldReturnBadRequestWhenOpenTimeIsNullOnPatch() throws Exception {
        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt(null)
                .closeAt("18:00:00")
                .build();

        List<BusinessHourDTORequest> businessHours = new ArrayList<>();
        businessHours.add(dto);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHours)
                .build();
        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Open time must be provided.")));
    }

    @Test
    void shouldReturnBadRequestWhenOpenTimeIsNotProvidedOnPatch() throws Exception {
        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .closeAt("18:00:00")
                .build();

        List<BusinessHourDTORequest> businessHours = new ArrayList<>();
        businessHours.add(dto);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHours)
                .build();
        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Open time must be provided.")));
    }

    @Test
    void shouldReturnBadRequestWhenCloseTimeIsInvalidOnPatch() throws Exception {
        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("08:00:00")
                .closeAt("24:00:00")
                .build();

        List<BusinessHourDTORequest> businessHours = new ArrayList<>();
        businessHours.add(dto);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHours)
                .build();
        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("businessHours[0].closeAt: Invalid closing time. Times must be in 24-hour format, i.e., HH:mm:ss (e.g., 08:00:00 or 23:59:00)")));
    }

    @Test
    void shouldReturnBadRequestWhenCloseTimeIsNullOnPatch() throws Exception {
        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("08:00:00")
                .closeAt(null)
                .build();

        List<BusinessHourDTORequest> businessHours = new ArrayList<>();
        businessHours.add(dto);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHours)
                .build();
        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Close time must be provided.")));
    }

    @Test
    void shouldReturnBadRequestWhenCloseTimeIsNotProvidedOnPatch() throws Exception {
        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("08:00:00")
                .build();

        List<BusinessHourDTORequest> businessHours = new ArrayList<>();
        businessHours.add(dto);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHours)
                .build();
        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Close time must be provided.")));
    }

    @Test
    void shouldReturnBadRequestWhenOpenAndCloseTimeAreInvalidOnPatch() throws Exception {
        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("08:00")
                .closeAt("24:00:00")
                .build();

        List<BusinessHourDTORequest> businessHours = new ArrayList<>();
        businessHours.add(dto);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHours)
                .build();
        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid opening time. Times must be in 24-hour format, i.e., HH:mm:ss (e.g., 08:00:00 or 23:59:00)")))
                .andExpect(content().string(containsString("Invalid closing time. Times must be in 24-hour format, i.e., HH:mm:ss (e.g., 08:00:00 or 23:59:00)")));
    }

    @Test
    void shouldReturnBadRequestWhenOpenAndCloseTimeAreNullOnPatch() throws Exception {
        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt(null)
                .closeAt(null)
                .build();

        List<BusinessHourDTORequest> businessHours = new ArrayList<>();
        businessHours.add(dto);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHours)
                .build();
        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Open time must be provided.")))
                .andExpect(content().string(containsString("Close time must be provided.")));
    }

    @Test
    void shouldReturnBadRequestWhenOpenAndCloseTimeAreNotProvidedOnPatch() throws Exception {
        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .build();

        List<BusinessHourDTORequest> businessHours = new ArrayList<>();
        businessHours.add(dto);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHours)
                .build();
        // Act & Assert
        mockMvc.perform(patch("/api/seller/patch/{code}", seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Open time must be provided.")))
                .andExpect(content().string(containsString("Close time must be provided.")));
    }

    @ParameterizedTest
    @MethodSource("geoCoordinatesProvider")
    void shouldReturnBadRequestWhenGeoCoordinatesAreOutOfRange(Double latitude, Double longitude, String expectedLatitudeError, String expectedLongitudeError) throws Exception {
        // Arrange
        SellerDTORequest request = SellerDTORequestBuilder.createDefault();

        GeoCoordinatesDTORequest geoCoordinates = GeoCoordinatesDTORequest.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();

        SellerDTORequest request2 = SellerDTORequestBuilder.createWithCustomGeoCoordinates(request, geoCoordinates);

        // Act & Assert
        mockMvc.perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(expectedLatitudeError)))
                .andExpect(content().string(containsString(expectedLongitudeError)));
    }


    private static Stream<Arguments> geoCoordinatesProvider() {
        return Stream.of(
                // Testes para Latitude
                Arguments.of(-100.0, -46.6333, "Latitude must be >= -90.0", ""),
                Arguments.of(100.0, -46.6333, "Latitude must be <= 90.0", ""),

                // Testes para Longitude
                Arguments.of(-23.5505, -200.0, "", "Longitude must be >= -180.0"),
                Arguments.of(-23.5505, 200.0, "", "Longitude must be <= 180.0"),

                // Testes para ambos
                Arguments.of(-100.0, 200.0, "Latitude must be >= -90.0", "Longitude must be <= 180.0")
        );
    }
}
