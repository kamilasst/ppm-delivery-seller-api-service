package com.ppm.delivery.seller.api.service.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ppm.delivery.seller.api.service.PpmDeliverySellerApiServiceApplication;
import com.ppm.delivery.seller.api.service.api.constants.HeaderConstants;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerNearSearchRequest;
import com.ppm.delivery.seller.api.service.builder.SellerBuilder;
import com.ppm.delivery.seller.api.service.constants.ConstantsMocks;
import com.ppm.delivery.seller.api.service.domain.model.BusinessHour;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import com.ppm.delivery.seller.api.service.domain.profile.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = {PpmDeliverySellerApiServiceApplication.class})
public class SellerAvailabilityApiIntegrationTest extends AbstractComponentTest {

    @Test
    void shouldReturnOkAndActiveSellersNear() throws Exception {

        //Arrange

        //Vendedor 1 (Ativo, dentro do raio, ABERTO no horário de teste)
        Seller seller1 = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_BR, Status.ACTIVE, ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_2, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_2);
        seller1.getBusinessHours().clear();
        List<BusinessHour> seller1BusinessHours = new ArrayList<>();
        BusinessHour bh1 = BusinessHour.builder()
                .dayOfWeek(DayOfWeek.FRIDAY.name())
                .openAt(ConstantsMocks.TIME_08h30m00)
                .closeAt(ConstantsMocks.TIME_18h00m00)
                .build();
        seller1BusinessHours.add(bh1);
        seller1.setBusinessHours(seller1BusinessHours);
        bh1.setSeller(seller1);

        //Vendedor 2 (Ativo, dentro do raio, FECHADO no horário de teste - abre Sábado)
        Seller seller2 = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_BR, Status.ACTIVE, ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_3, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_3);
        seller2.getBusinessHours().clear();
        List<BusinessHour> seller2BusinessHours = new ArrayList<>();
        BusinessHour bh2 = BusinessHour.builder()
                .dayOfWeek(DayOfWeek.SATURDAY.name())
                .openAt(ConstantsMocks.TIME_08h30m00)
                .closeAt(ConstantsMocks.TIME_18h00m00)
                .build();
        seller2BusinessHours.add(bh2);
        seller2.setBusinessHours(seller2BusinessHours);
        bh2.setSeller(seller2);

        //Vendedor 3: Inativo, mesmo que perto (não deve ser retornado)
        Seller seller3 = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_BR, Status.INACTIVE, ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_4, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_4);

        //Vendedor 4: Ativo, mas longe (não deve ser retornado)
        Seller seller4 = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_BR, Status.ACTIVE,  ConstantsMocks.LATITUDE_RECIFE_BOAVIAGEM_1, ConstantsMocks.LONGITUDE_RECIFE_BOAVIAGEM_1);

        //Vendedor 5: Ativo, dentro do raio, no horário do teste, fora do país (não deve ser retornado se o filtro de país estiver ativo)
        Seller seller5 = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_AR, Status.ACTIVE,  ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_2, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_2);
        List<BusinessHour> seller5BusinessHours = new ArrayList<>();
        BusinessHour bh5 = BusinessHour.builder()
                .dayOfWeek(DayOfWeek.FRIDAY.name())
                .openAt(ConstantsMocks.TIME_08h00m00)
                .closeAt(ConstantsMocks.TIME_18h00m00)
                .build();
        seller5BusinessHours.add(bh5);
        seller5.setBusinessHours(seller5BusinessHours);
        bh5.setSeller(seller5);

        //Vendedor 6 (Ativo, dentro do raio, ABERTO no horário de teste)
        Seller seller6 = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_BR, Status.ACTIVE,  ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_2, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_2);
        seller6.getBusinessHours().clear();

        List<BusinessHour> seller6BusinessHours = new ArrayList<>();
        BusinessHour bh3 = BusinessHour.builder()
                .dayOfWeek(DayOfWeek.FRIDAY.name())
                .openAt(ConstantsMocks.TIME_08h00m00)
                .closeAt(ConstantsMocks.TIME_18h00m00)
                .build();
        seller6BusinessHours.add(bh3);
        seller6.setBusinessHours(seller6BusinessHours);
        bh3.setSeller(seller6);

        //Vendedor 7 (Ativo, Mesma LAT e LOG do filtro, Aberto no horario) -- 08 as 12
        Seller seller7 = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_BR, Status.ACTIVE,  ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_1, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_1);
        seller7.getBusinessHours().clear();

        List<BusinessHour> seller7BusinessHours = new ArrayList<>();
        BusinessHour bh4 = BusinessHour.builder()
                .dayOfWeek(DayOfWeek.FRIDAY.name())
                .openAt(ConstantsMocks.TIME_08h00m00)
                .closeAt(ConstantsMocks.TIME_12h00m00)
                .build();
        seller7BusinessHours.add(bh4);
        seller7.setBusinessHours(seller7BusinessHours);
        bh4.setSeller(seller7);

        sellerRepository.saveAll(List.of(seller1, seller2, seller3, seller4, seller5, seller6, seller7));

        // --- Data e hora do pedido para o teste ---
        // Simula um pedido criado na Sexta-feira, 13 de junho de 2025, às 10:00 AM
        LocalDateTime orderTime = LocalDateTime.of(2025, 6, 13, 10, 0, 0);

        SellerNearSearchRequest request = new SellerNearSearchRequest(
                orderTime,
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

        assertFalse(responseSellers.isEmpty(), "The seller list must not be empty.");
        assertEquals(3, responseSellers.size(), "Should return only 3 active sellers that are nearby and open at the given time.");
        assertEquals(seller1, responseSellers.get(0), "The returned seller should be seller1 (active, within range, and open).");
        assertEquals(seller6, responseSellers.get(1), "The returned seller should be seller6 (active, within range, and open).");
        assertEquals(seller7, responseSellers.get(2), "The returned seller should be seller7 (active, within range, and open).");

    }

    @Test
    void shouldReturnEmptyListWhenNoSellersMatchCriteria() throws Exception {
        // Arrange

        //Vendedor fora do país
        Seller seller1 = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_AR, Status.ACTIVE, ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_1, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_1);
        seller1.getBusinessHours().clear();
        BusinessHour bh1 = BusinessHour.builder()
                .dayOfWeek(DayOfWeek.FRIDAY.name())
                .openAt(ConstantsMocks.TIME_08h00m00)
                .closeAt(ConstantsMocks.TIME_18h00m00)
                .build();
        bh1.setSeller(seller1);
        seller1.setBusinessHours(List.of(bh1));

        // Vendedor inativo (mesmo no país e horário certo, mas status inválido)
        Seller seller2 = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_BR, Status.INACTIVE, ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_1, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_1);
        seller2.getBusinessHours().clear();
        BusinessHour bh2 = BusinessHour.builder()
                .dayOfWeek(DayOfWeek.FRIDAY.name())
                .openAt(ConstantsMocks.TIME_08h00m00)
                .closeAt(ConstantsMocks.TIME_18h00m00)
                .build();
        bh2.setSeller(seller2);
        seller2.setBusinessHours(List.of(bh2));

        // Vendedor ativo, mas só abre aos sábados
        Seller seller3 = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_BR, Status.ACTIVE, ConstantsMocks.LATITUDE_RECIFE_BOAVISTA_1, ConstantsMocks.LONGITUDE_RECIFE_BOAVISTA_1);
        seller3.getBusinessHours().clear();
        BusinessHour bh3 = BusinessHour.builder()
                .dayOfWeek(DayOfWeek.SATURDAY.name())
                .openAt(ConstantsMocks.TIME_08h00m00)
                .closeAt(ConstantsMocks.TIME_18h00m00)
                .build();
        bh3.setSeller(seller3);
        seller3.setBusinessHours(List.of(bh3));

        // Vendedor ativo, no país, mas está longe (fora do raio)
        Seller seller4 = SellerBuilder.createWithCoordinates(ConstantsMocks.COUNTRY_CODE_BR, Status.ACTIVE, ConstantsMocks.LATITUDE_SAO_PAULO, ConstantsMocks.LONGITUDE_SAO_PAULO);
        seller4.getBusinessHours().clear();
        BusinessHour bh4 = BusinessHour.builder()
                .dayOfWeek(DayOfWeek.FRIDAY.name())
                .openAt(ConstantsMocks.TIME_08h00m00)
                .closeAt(ConstantsMocks.TIME_18h00m00)
                .build();
        bh4.setSeller(seller4);
        seller4.setBusinessHours(List.of(bh4));

        sellerRepository.saveAll(List.of(seller1, seller2, seller3, seller4));

        // --- Data e hora do pedido para o teste ---
        LocalDateTime orderTime = LocalDateTime.of(2025, 6, 13, 10, 0, 0); // Sexta, 10h

        SellerNearSearchRequest request = new SellerNearSearchRequest(
                orderTime,
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
        assertTrue(responseBody.isEmpty(), "Expected no sellers to be returned.");

    }

    @Test
    void shouldReturnBadRequestWhenRequiredFieldsAreMissingOrInvalid() throws Exception {
        // Arrange: JSON com campos inválidos (todos nulos ou fora da faixa permitida)
        String invalidRequestJson = """
        {
            "orderCreateDate": null,
            "orderDeliveryInfo": {
                "latitude": -999,
                "longitude": null
            },
            "radius": 0
        }
        """;

        // Act & Assert
        var resultActions = mockMvc.perform(post(ConstantsMocks.URI_TEMPLATE_POST_AVAILABLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.USER.name())
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("orderCreateDate: orderCreateDate is required")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("orderDeliveryInfo.latitude: Latitude must be >= -90.0")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("orderDeliveryInfo.longitude: longitude is required")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("radius: Radius must be at least 1 meter")));

    }

}

