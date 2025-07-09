package com.ppm.delivery.seller.api.service.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ppm.delivery.seller.api.service.PpmDeliverySellerApiServiceApplication;
import com.ppm.delivery.seller.api.service.api.constants.HeaderConstants;
import com.ppm.delivery.seller.api.service.api.domain.request.BusinessHourDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.GeoCoordinatesDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.builder.SellerBuilder;
import com.ppm.delivery.seller.api.service.builder.SellerDTORequestBuilder;
import com.ppm.delivery.seller.api.service.builder.SellerInvalidJsonSamples;
import com.ppm.delivery.seller.api.service.constants.ConstantsMocks;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import com.ppm.delivery.seller.api.service.domain.profile.Profile;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
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
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = {PpmDeliverySellerApiServiceApplication.class})
class SellerPostCreateComponentTest extends AbstractComponentTest {

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
                        post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
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
                        post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
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
    void shouldSuccessfullyPostWhenBusinessHoursIsNullOnPost() throws Exception {

        // arrange
        SellerDTORequest request = SellerDTORequestBuilder.createWithNullBusinessHours();
        String countryCode = ConstantsMocks.COUNTRY_CODE_BR;
        Seller expectedSeller = SellerBuilder.createAllowingNullBusinessHours(countryCode, request);

        // act
        var resultActions = mockMvc
                .perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
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
                .perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
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
        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
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
        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
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
                .openAt(ConstantsMocks.TIME_08h00m00)
                .closeAt(ConstantsMocks.TIME_18h00m00)
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);

        // Act & Assert
        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_DAY_OF_WEEK_MUST_BE_PROVIDED)));
    }

    @Test
    void shouldReturnBadRequestWhenDayOfWeekIsInvalidOnPost() throws Exception {

        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content( ConstantsMocks.JSON_INVALID_REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Error JSON Inválido - JSON parse error: Cannot deserialize value of type `java.time.DayOfWeek` from String")));
    }

    @Test
    void shouldReturnBadRequestWhenDayOfWeekIsBlankOnPost() throws Exception {

        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(ConstantsMocks.JSON_EMPTY_REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Error JSON Inválido - JSON parse error: Cannot coerce empty String (\\\"\\\") to `java.time.DayOfWeek`")));
    }

    @Test
    void shouldReturnBadRequestWhenDayOfWeekIsNotProvidedOnPost() throws Exception {
        // Arrange
        BusinessHourDTORequest businessHour = BusinessHourDTORequest.builder()
                .openAt(ConstantsMocks.TIME_08h00m00)
                .closeAt(ConstantsMocks.TIME_18h00m00)
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);

        // Act & Assert
        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_DAY_OF_WEEK_MUST_BE_PROVIDED)));
    }

    @Test
    void shouldReturnBadRequestWhenOpenTimeIsInvalidOnPost() throws Exception {
        // Arrange
        BusinessHourDTORequest businessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt(ConstantsMocks.TIME_08h00)
                .closeAt(ConstantsMocks.TIME_18h00m00)
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);


        // Act & Assert
        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
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
                .closeAt(ConstantsMocks.TIME_18h00m00)
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);


        // Act & Assert
        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_OPEN_TIME_MUST_BE_PROVIDED)));
    }

    @Test
    void shouldReturnBadRequestWhenOpenTimeIsNotProvidesOnPost() throws Exception {
        // Arrange
        BusinessHourDTORequest businessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .closeAt(ConstantsMocks.TIME_18h00m00)
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);


        // Act & Assert
        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_OPEN_TIME_MUST_BE_PROVIDED)));
    }

    @Test
    void shouldReturnBadRequestWhenCloseTimeIsInvalidOnPost() throws Exception {
        // Arrange
        BusinessHourDTORequest businessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt(ConstantsMocks.TIME_08h00m00)
                .closeAt(ConstantsMocks.TIME_24h00m00)
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);


        // Act & Assert
        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
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
                .openAt(ConstantsMocks.TIME_08h00m00)
                .closeAt(null)
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);


        // Act & Assert
        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_CLOSE_TIME_MUST_BE_PROVIDED)));
    }

    @Test
    void shouldReturnBadRequestWhenCloseTimeIsNotProvidedOnPost() throws Exception {
        // Arrange
        BusinessHourDTORequest businessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt(ConstantsMocks.TIME_08h00m00)
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);


        // Act & Assert
        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_CLOSE_TIME_MUST_BE_PROVIDED)));
    }

    @Test
    void shouldReturnBadRequestWhenOpenAndCloseTimeAreInvalidOnPost() throws Exception {
        // Arrange
        BusinessHourDTORequest businessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt(ConstantsMocks.TIME_08h00)
                .closeAt(ConstantsMocks.TIME_24h00m00)
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(businessHour);


        // Act & Assert
        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
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
        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_OPEN_TIME_MUST_BE_PROVIDED)))
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_CLOSE_TIME_MUST_BE_PROVIDED)));
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
        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_OPEN_TIME_MUST_BE_PROVIDED)))
                .andExpect(content().string(containsString(MessageErrorConstants.ERROR_CLOSE_TIME_MUST_BE_PROVIDED)));
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
        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
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
                Arguments.of(-100.0, -46.6333, "Latitude must be >= -90.0", ""),
                Arguments.of(100.0, -46.6333, "Latitude must be <= 90.0", ""),

                Arguments.of(-23.5505, -200.0, "", "Longitude must be >= -180.0"),
                Arguments.of(-23.5505, 200.0, "", "Longitude must be <= 180.0"),

                Arguments.of(-100.0, 200.0, "Latitude must be >= -90.0", "Longitude must be <= 180.0")
        );
    }

    @Test
    void shouldReturnBadRequestWhenCloseTimeIsBeforeOpenTime() throws Exception {
        // arrange
        BusinessHourDTORequest invalidBusinessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt(ConstantsMocks.TIME_18h00m00)
                .closeAt(ConstantsMocks.TIME_10h00m00)
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(invalidBusinessHour);

        // Act & Assert
        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("businessHours[0]: Opening time must be before closing time"
                )));
    }
}
