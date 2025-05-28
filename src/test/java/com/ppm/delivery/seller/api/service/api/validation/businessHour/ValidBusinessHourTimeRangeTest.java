package com.ppm.delivery.seller.api.service.api.validation.businessHour;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ppm.delivery.seller.api.service.api.domain.request.BusinessHourDTORequest;
import com.ppm.delivery.seller.api.service.constants.ConstantsMocks;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.DayOfWeek;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidBusinessHourTimeRangeTest {

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

    @ParameterizedTest
    @MethodSource("provideTrueBusinessHourValidationCases")
    void shouldTrueValidateBusinessHourTimeRange(BusinessHourDTORequest dto, String expectedMessage) {
        // Act
        Set<ConstraintViolation<BusinessHourDTORequest>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(expectedMessage);
    }

    @ParameterizedTest
    @MethodSource("provideFalseBusinessHourValidationCases")
    void shouldFalseValidateBusinessHourTimeRange(BusinessHourDTORequest dto) {
        // Act
        Set<ConstraintViolation<BusinessHourDTORequest>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isEmpty();
    }

    private static Stream<Arguments> provideFalseBusinessHourValidationCases() {
        return Stream.of(
                Arguments.of(
                        BusinessHourDTORequest.builder()
                                .dayOfWeek(DayOfWeek.MONDAY)
                                .openAt(ConstantsMocks.TIME_08h00m00)
                                .closeAt(ConstantsMocks.TIME_18h00m00)
                                .build()
                )
        );
    }

    private static Stream<Arguments> provideTrueBusinessHourValidationCases() {
        return Stream.of(
                Arguments.of(
                        BusinessHourDTORequest.builder()
                                .dayOfWeek(DayOfWeek.MONDAY)
                                .openAt(ConstantsMocks.TIME_18h00m00)
                                .closeAt(ConstantsMocks.TIME_08h00m00)
                                .build(),
                        MessageErrorConstants.ERROR_OPENING_TIME_MUST_BE_BEFORE_CLOSING_TIME
                ),
                Arguments.of(
                        BusinessHourDTORequest.builder()
                                .dayOfWeek(DayOfWeek.MONDAY)
                                .openAt(ConstantsMocks.TIME_08h00m00)
                                .closeAt(ConstantsMocks.TIME_08h00m00)
                                .build(),
                        MessageErrorConstants.ERROR_OPENING_TIME_MUST_BE_BEFORE_CLOSING_TIME
                ),
                Arguments.of(
                        BusinessHourDTORequest.builder()
                                .dayOfWeek(DayOfWeek.MONDAY)
                                .openAt(ConstantsMocks.TIME_23h59m59)
                                .closeAt(ConstantsMocks.TIME_23h59m59)
                                .build(),
                        MessageErrorConstants.ERROR_OPENING_TIME_MUST_BE_BEFORE_CLOSING_TIME
                )
        );
    }
}