package com.ppm.delivery.seller.api.service.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ppm.delivery.seller.api.service.PpmDeliverySellerApiServiceApplication;
import com.ppm.delivery.seller.api.service.api.constants.HeaderConstants;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerNearSearchRequest;
import com.ppm.delivery.seller.api.service.builder.BusinessHourTestBuilder;
import com.ppm.delivery.seller.api.service.builder.SellerBuilder;
import com.ppm.delivery.seller.api.service.constants.ConstantsMocks;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import com.ppm.delivery.seller.api.service.domain.profile.Profile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;


@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = {PpmDeliverySellerApiServiceApplication.class})
public class SellerGetAvailableComponentTest extends AbstractComponentTest {

    @Test
    @DisplayName("Returns ACTIVE sellers within search radius and open at specified order time")
    void shouldReturnOkGetAvailableSellers() throws Exception {
        //Arrange
        Seller sellerActiveInRangeOpenNow = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_BR, Status.ACTIVE, ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_2, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_2);
        sellerActiveInRangeOpenNow.setBusinessHours(
                BusinessHourTestBuilder.defaultSchedule()
                        .withCustomTime(DayOfWeek.FRIDAY, ConstantsMocks.TIME_08h30m00, ConstantsMocks.TIME_18h00m00)
                        .forSeller(sellerActiveInRangeOpenNow)
                        .build()
        );

        Seller sellerInactiveInRange = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_BR, Status.INACTIVE, ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_4, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_4);

        Seller sellerActiveOutOfRange = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_BR, Status.ACTIVE, ConstantsMocks.LATITUDE_RECIFE_BOAVIAGEM_1, ConstantsMocks.LONGITUDE_RECIFE_BOAVIAGEM_1);

        Seller sellerForeignCountry = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_AR, Status.ACTIVE, ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_2, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_2);
        sellerForeignCountry.setBusinessHours(
                BusinessHourTestBuilder.defaultSchedule()
                        .withCustomTime(DayOfWeek.FRIDAY, ConstantsMocks.TIME_08h00m00, ConstantsMocks.TIME_18h00m00)
                        .forSeller(sellerForeignCountry)
                        .build()
        );

        Seller sellerActiveInRangeOpenNow2 = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_BR, Status.ACTIVE, ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_2, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_2);
        sellerActiveInRangeOpenNow2.setBusinessHours(
                BusinessHourTestBuilder.defaultSchedule()
                        .withCustomTime(DayOfWeek.FRIDAY, ConstantsMocks.TIME_08h00m00, ConstantsMocks.TIME_18h00m00)
                        .forSeller(sellerActiveInRangeOpenNow2)
                        .build()
        );

        Seller sellerSameLocationOpen = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_BR, Status.ACTIVE, ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_1, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_1);
        sellerSameLocationOpen.setBusinessHours(
                BusinessHourTestBuilder.defaultSchedule()
                        .withCustomTime(DayOfWeek.FRIDAY, ConstantsMocks.TIME_08h00m00, ConstantsMocks.TIME_12h00m00)
                        .forSeller(sellerSameLocationOpen)
                        .build()
        );

        sellerRepository.saveAll(List.of(
                sellerActiveInRangeOpenNow,
                sellerInactiveInRange,
                sellerActiveOutOfRange,
                sellerForeignCountry,
                sellerActiveInRangeOpenNow2,
                sellerSameLocationOpen));

        LocalDateTime requestedOrderDateTime = LocalDateTime.of(2025, 6, 13, 10, 0, 0);

        SellerNearSearchRequest request = new SellerNearSearchRequest(
                requestedOrderDateTime,
                new SellerNearSearchRequest.DeliveryInfoDTO(ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_1, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_1),
                ConstantsMocks.RAIO_2KM,
                null
        );

        //Act
        var resultActions = mockMvc
                .perform(
                        post(ConstantsMocks.URI_TEMPLATE_POST_AVAILABLE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                                .header(HeaderConstants.HEADER_PROFILE, Profile.USER.name())
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        //Assert
        List<Seller> responseSellers = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsString(),
                new TypeReference<List<Seller>>() {
                }
        );

        responseSellers.forEach(s -> System.out.println("Returned: " + s.getCode() + " - " + s.getStatus()));

        assertFalse(responseSellers.isEmpty(), "The seller list must not be empty.");
        assertEquals(3, responseSellers.size(), "Should return only 3 active sellers that are nearby and open at the given time.");
        assertThat(responseSellers, containsInAnyOrder(
                sellerActiveInRangeOpenNow,
                sellerActiveInRangeOpenNow2,
                sellerSameLocationOpen
        ));


    }

    @Test
    @DisplayName("Returns empty list when no sellers match the search criteria")
    void shouldReturnEmptyListWhenNoSellersMatchCriteria() throws Exception {
        // Arrange
        Seller sellerOutsideCountry = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_AR, Status.ACTIVE, ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_1, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_1);
        sellerOutsideCountry.setBusinessHours(
                BusinessHourTestBuilder.defaultSchedule()
                        .withCustomTime(DayOfWeek.FRIDAY, ConstantsMocks.TIME_08h00m00, ConstantsMocks.TIME_18h00m00)
                        .forSeller(sellerOutsideCountry)
                        .build()
        );

        Seller sellerInactive = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_BR, Status.INACTIVE, ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_1, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_1);
        sellerInactive.setBusinessHours(
                BusinessHourTestBuilder.defaultSchedule()
                        .withCustomTime(DayOfWeek.FRIDAY, ConstantsMocks.TIME_08h00m00, ConstantsMocks.TIME_18h00m00)
                        .forSeller(sellerInactive)
                        .build()
        );

        Seller sellerTooFarFromLocation = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_BR, Status.ACTIVE, ConstantsMocks.LATITUDE_SAO_PAULO, ConstantsMocks.LONGITUDE_SAO_PAULO);
        sellerTooFarFromLocation.setBusinessHours(
                BusinessHourTestBuilder.defaultSchedule()
                        .withCustomTime(DayOfWeek.FRIDAY, ConstantsMocks.TIME_08h00m00, ConstantsMocks.TIME_18h00m00)
                        .forSeller(sellerTooFarFromLocation)
                        .build()
        );

        sellerRepository.saveAll(List.of(
                sellerOutsideCountry,
                sellerInactive,
                sellerTooFarFromLocation));

        LocalDateTime requestedOrderDateTime = LocalDateTime.of(2025, 6, 13, 10, 0, 0); // Sexta, 10h

        SellerNearSearchRequest request = new SellerNearSearchRequest(
                requestedOrderDateTime,
                new SellerNearSearchRequest.DeliveryInfoDTO(ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_1, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_1),
                ConstantsMocks.RAIO_2KM,
                null
        );

        // Act
        var resultActions = mockMvc
                .perform(
                        post(ConstantsMocks.URI_TEMPLATE_POST_AVAILABLE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                                .header(HeaderConstants.HEADER_PROFILE, Profile.USER.name())
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        // Assert
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body: " + responseBody);
        assertTrue(responseBody.isEmpty(), "Expected no sellers to be returned.");
    }

    @Test
    @DisplayName("Returns Bad Request when required fields are missing or invalid")
    void shouldReturnBadRequestWhenRequiredFieldsAreMissingOrInvalid() throws Exception {
        // Arrange
        SellerNearSearchRequest invalidRequest = new SellerNearSearchRequest(
                null,
                new SellerNearSearchRequest.DeliveryInfoDTO(-999.0, null),
                0.0,
                null
        );

        // Act & Assert
        mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_AVAILABLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.USER.name())
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("orderCreateDate: orderCreateDate is required")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("orderDeliveryInfo.latitude: Latitude must be >= -90.0")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("orderDeliveryInfo.longitude: longitude is required")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("radius: Radius must be at least 1 meter")));
    }

}

