package com.ppm.delivery.seller.api.service.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ppm.delivery.seller.api.service.PpmDeliverySellerApiServiceApplication;
import com.ppm.delivery.seller.api.service.api.constants.HeaderConstants;
import com.ppm.delivery.seller.api.service.api.domain.request.BusinessHourDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerUpdateDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerUpdateDTOResponse;
import com.ppm.delivery.seller.api.service.builder.SellerBuilder;
import com.ppm.delivery.seller.api.service.domain.model.BusinessHour;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import com.ppm.delivery.seller.api.service.domain.profile.Profile;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import com.ppm.delivery.seller.api.service.constants.ConstantsMocks;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = {PpmDeliverySellerApiServiceApplication.class})
public class SellerUpdateApiIntegrationTest extends AbstractComponentTest{

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
                        patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
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
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.USER.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ConstantsMocks.JSON_PATH_ERROR).value(MessageErrorConstants.ERROR_OPERATION_NOT_PERMITTED_FOR_THIS_PROFILE));
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
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, " ")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ConstantsMocks.JSON_PATH_ERROR).value(MessageErrorConstants.ERROR_PROFILE_REQUIRED_HEADER));
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
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, ConstantsMocks.JSON_INVALID_PROFILE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ConstantsMocks.JSON_PATH_ERROR).value(MessageErrorConstants.ERROR_PROFILE_IS_INVALID));
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
                        .openAt(ConstantsMocks.TIME_08h30m00)
                        .closeAt(ConstantsMocks.TIME_18h00m00).build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openAt(ConstantsMocks.TIME_09h00m00)
                        .closeAt(ConstantsMocks.TIME_18h00m00).build());

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHoursList)
                .build();

        // Act
        var resultActions = mockMvc
                .perform(
                        patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
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
        assertEquals(ConstantsMocks.TIME_08h30m00, updatedSunday.getOpenAt());
        assertEquals(ConstantsMocks.TIME_18h00m00, updatedSunday.getCloseAt());

        BusinessHour updatedMonday = updatedSeller.get().getBusinessHours().stream()
                .filter(bh -> DayOfWeek.MONDAY.name().equals(bh.getDayOfWeek()))
                .findFirst().orElseThrow();
        assertEquals(ConstantsMocks.TIME_09h00m00, updatedMonday.getOpenAt());
        assertEquals(ConstantsMocks.TIME_18h00m00, updatedMonday.getCloseAt());
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
                        .openAt(ConstantsMocks.TIME_08h30m00)
                        .closeAt(ConstantsMocks.TIME_18h00m00).build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openAt(ConstantsMocks.TIME_09h00m00)
                        .closeAt(ConstantsMocks.TIME_18h00m00).build());

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHoursList)
                .build();

        // Act
        var resultActions = mockMvc
                .perform(
                        patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
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
        assertEquals(ConstantsMocks.TIME_08h30m00, updatedSunday.getOpenAt());
        assertEquals(ConstantsMocks.TIME_18h00m00, updatedSunday.getCloseAt());

        BusinessHour updatedMonday = updatedSeller.get().getBusinessHours().stream()
                .filter(bh -> DayOfWeek.MONDAY.name().equals(bh.getDayOfWeek()))
                .findFirst().orElseThrow();
        assertEquals(ConstantsMocks.TIME_09h00m00, updatedMonday.getOpenAt());
        assertEquals(ConstantsMocks.TIME_18h00m00, updatedMonday.getCloseAt());
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
                        .openAt(ConstantsMocks.TIME_08h30m00)
                        .closeAt(ConstantsMocks.TIME_18h00m00).build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openAt(ConstantsMocks.TIME_09h00m00)
                        .closeAt(ConstantsMocks.TIME_18h00m00).build());

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHoursList)
                .status(Status.ACTIVE)
                .build();

        // Act
        var resultActions = mockMvc
                .perform(
                        patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
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
        assertEquals(ConstantsMocks.TIME_08h30m00, updatedSunday.getOpenAt());
        assertEquals(ConstantsMocks.TIME_18h00m00, updatedSunday.getCloseAt());

        BusinessHour updatedMonday = updatedSeller.get().getBusinessHours().stream()
                .filter(bh -> DayOfWeek.MONDAY.name().equals(bh.getDayOfWeek()))
                .findFirst().orElseThrow();
        assertEquals(ConstantsMocks.TIME_09h00m00, updatedMonday.getOpenAt());
        assertEquals(ConstantsMocks.TIME_18h00m00, updatedMonday.getCloseAt());
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
                        .openAt(ConstantsMocks.TIME_08h30m00)
                        .closeAt(ConstantsMocks.TIME_18h00m00).build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openAt(ConstantsMocks.TIME_09h00m00)
                        .closeAt(ConstantsMocks.TIME_18h00m00).build());

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHoursList)
                .build();

        // Act & Assert
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, " ")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ConstantsMocks.JSON_PATH_ERROR).value(MessageErrorConstants.ERROR_PROFILE_REQUIRED_HEADER));
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
                        .openAt(ConstantsMocks.TIME_08h30m00)
                        .closeAt(ConstantsMocks.TIME_18h00m00).build(),
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openAt(ConstantsMocks.TIME_09h00m00)
                        .closeAt(ConstantsMocks.TIME_18h00m00).build());

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHoursList)
                .build();

        // Act & Assert
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, ConstantsMocks.JSON_INVALID_PROFILE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ConstantsMocks.JSON_PATH_ERROR).value(MessageErrorConstants.ERROR_PROFILE_IS_INVALID));
    }

    @Test
    void shouldReturnBadRequestWhenStatusAndBusinessHoursAreNull() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);
        sellerRepository.save(seller);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(null)
                .businessHours(null)
                .build();

        // Act & Assert
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ConstantsMocks.JSON_PATH_ERROR).value(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED));
    }

    @Test
    void shouldReturnNotFoundWhenSellerCodeIsNotFound() throws Exception {

        // Arrange
        String nonExistentCode = "123456";
        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .status(Status.ACTIVE).build();

        // Act & Assert
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, nonExistentCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ConstantsMocks.JSON_PATH_ERROR).value(MessageErrorConstants.ERROR_SELLER_NOT_FOUND));
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsNullOnPatch() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);

        // Act & Assert
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ConstantsMocks.JSON_PATH_ERROR).value(MessageErrorConstants.ERROR_REQUEST_BODY_IS_REQUIRED));
    }


    @Test
    void shouldReturnBadRequestWhenBusinessHoursIsNullOnPatch() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);
        sellerRepository.save(seller);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(null)
                .build();

        // Act & Assert
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ConstantsMocks.JSON_PATH_ERROR).value(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED));
    }

    @Test
    void shouldReturnBadRequestWhenBusinessHoursIsNotProvidedOnPatch() throws Exception {

        // Arrange
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller seller = SellerBuilder.createDefault(countryCode);
        sellerRepository.save(seller);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .build();

        // Act & Assert
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ConstantsMocks.JSON_PATH_ERROR).value(MessageErrorConstants.ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED));
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
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
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
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
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
                .openAt(ConstantsMocks.TIME_08h00m00)
                .closeAt(ConstantsMocks.TIME_18h00m00)
                .build();

        List<BusinessHourDTORequest> businessHours = new ArrayList<>();
        businessHours.add(dto);

        SellerUpdateDTORequest request = SellerUpdateDTORequest.builder()
                .businessHours(businessHours)
                .build();
        // Act & Assert
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_DAY_OF_WEEK_MUST_BE_PROVIDED)));
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
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_DAY_OF_WEEK_MUST_BE_PROVIDED)));
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

        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
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

        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
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
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
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
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_OPEN_TIME_MUST_BE_PROVIDED)));
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
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_OPEN_TIME_MUST_BE_PROVIDED)));
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
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
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
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_CLOSE_TIME_MUST_BE_PROVIDED)));
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
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_CLOSE_TIME_MUST_BE_PROVIDED)));
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
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
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
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_OPEN_TIME_MUST_BE_PROVIDED)))
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_CLOSE_TIME_MUST_BE_PROVIDED)));
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
        mockMvc.perform(patch(ConstantsMocks.URI_TEMPLATE_PATCH_UPDATE, seller.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_OPEN_TIME_MUST_BE_PROVIDED)))
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_CLOSE_TIME_MUST_BE_PROVIDED)));
    }

}
