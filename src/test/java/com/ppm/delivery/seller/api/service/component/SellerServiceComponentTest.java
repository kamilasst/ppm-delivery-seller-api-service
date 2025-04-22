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
import com.ppm.delivery.seller.api.service.domain.profile.Profile;
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
                new TypeReference<>() {});

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
                                .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
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
    void shouldSuccessfullyPatchSellerOnlyBusinessHoursWhenProfileIsUser() throws Exception {

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
                                .header(HeaderConstants.HEADER_PROFILE, Profile.USER.name())
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
    void shouldSuccessfullyPatchSellerStatusAndBusinessHoursWhenProfileIsAdmin() throws Exception {

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
                                .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
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
    void shouldReturnBadRequestWhenPatchBusinessHoursProfileIsBlank() throws Exception {

        // Arrange
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
    void shouldReturnBadRequestWhenRequestIsNull() throws Exception {

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
                .andExpect(jsonPath("$.error").value(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED));
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
    void shouldReturnBadRequestWhenStatusIsNullAndBusinessHoursIsEmpty() throws Exception {

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
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED));
    }

}
