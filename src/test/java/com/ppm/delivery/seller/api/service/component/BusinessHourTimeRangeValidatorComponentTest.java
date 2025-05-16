package com.ppm.delivery.seller.api.service.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ppm.delivery.seller.api.service.PpmDeliverySellerApiServiceApplication;
import com.ppm.delivery.seller.api.service.api.constants.HeaderConstants;
import com.ppm.delivery.seller.api.service.api.domain.request.BusinessHourDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.builder.SellerDTORequestBuilder;
import com.ppm.delivery.seller.api.service.domain.profile.Profile;
import com.ppm.delivery.seller.api.service.utils.ConstantsMocks;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO atg ReviewCode POST: No caso dessa classe, houve uma pequena confusao entre testes unitário e de componente
// nesse caso entendo que é uma classe de test unitário, e deveria está fora do pacote de testes de componente.
// Crie no pacote de testes um pacote "validation.businessHour" e dentro dele, adicione essa classe renomeando para
// BusinessHourTimeRangeValidatorTest, pois se trata de um teste unitário
@ActiveProfiles("test")
@SpringBootTest(classes = {PpmDeliverySellerApiServiceApplication.class})
public class BusinessHourTimeRangeValidatorComponentTest {

    @Autowired
    private MockMvc mockMvc;

    private Validator validator;
    protected static ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeAll
    public static void setUpOnce() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // TODO atg ReviewCode POST: Esse é um teste de componente do POST, deveria está dentro da classe SellerServiceComponentTest
    // e depois da refatoracao proposta la, SellerServiceComponentPOSTTest
    @Test
    void shouldReturnBadRequestWhenCloseTimeIsBeforeOpenTime() throws Exception {
        // arrange
        BusinessHourDTORequest invalidBusinessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("15:00:00")   // TODO atg ReviewCode POST: Criar constante para o horário
                .closeAt("10:00:00")
                .build();

        SellerDTORequest request = SellerDTORequestBuilder.createDefault();
        request.businessHours().clear();
        request.businessHours().add(invalidBusinessHour);

        // TODO atg ReviewCode POST: Criar constante
        // Act & Assert
        mockMvc.perform(post("/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HeaderConstants.HEADER_COUNTRY, ConstantsMocks.COUNTRY_CODE_BR)
                        .header(HeaderConstants.HEADER_PROFILE, Profile.ADMIN.name())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("businessHours[0]: Opening time must be before closing time"
                )));
    }

    @Test
    void shouldReturnNoViolationsWhenOpenTimeIsBeforeCloseTime() {
        // arrange
        BusinessHourDTORequest validBusinessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("08:00:00")  // TODO atg ReviewCode POST: Criar constante em ConstantsMocks
                .closeAt("18:00:00")
                .build();

        // Act
        Set<ConstraintViolation<BusinessHourDTORequest>> violations = validator.validate(validBusinessHour);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldReturnViolationWhenCloseTimeIsBeforeOpenTime() {
        // arrange
        BusinessHourDTORequest invalidBusinessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("18:00:00")  // TODO atg ReviewCode POST: Criar constante em ConstantsMocks
                .closeAt("08:00:00")
                .build();

        // Act
        Set<ConstraintViolation<BusinessHourDTORequest>> violations = validator.validate(invalidBusinessHour);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Opening time must be before closing time");
    }

    @Test
    void shouldReturnViolationWhenOpenTimeIsEqualToCloseTime() {
        // arrange
        BusinessHourDTORequest invalidBusinessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("08:00:00")  // TODO atg ReviewCode POST: Criar constante em ConstantsMocks
                .closeAt("08:00:00")
                .build();

        // Act
        Set<ConstraintViolation<BusinessHourDTORequest>> violations = validator.validate(invalidBusinessHour);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);

        // TODO atg ReviewCode POST: Reutilizar a constante do @interface ValidBusinessHourTimeRange ?
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Opening time must be before closing time");
    }

    @Test
    void shouldReturnViolationWhenOpenTimeIsEqualToCloseTimeOnDayBoundary() {
        // arrange
        BusinessHourDTORequest invalidBusinessHour = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("23:59:59")  // TODO atg ReviewCode POST: Criar constante em ConstantsMocks
                .closeAt("23:59:59")
                .build();

        // Act
        Set<ConstraintViolation<BusinessHourDTORequest>> violations = validator.validate(invalidBusinessHour);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);

        // TODO atg ReviewCode POST: Reutilizar a constante do @interface ValidBusinessHourTimeRange ?
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Opening time must be before closing time");
    }

}