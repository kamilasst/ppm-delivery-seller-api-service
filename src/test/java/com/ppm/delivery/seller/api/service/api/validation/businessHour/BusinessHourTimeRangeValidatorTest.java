package com.ppm.delivery.seller.api.service.api.validation.businessHour;

import com.ppm.delivery.seller.api.service.api.domain.request.BusinessHourDTORequest;
import com.ppm.delivery.seller.api.service.constants.ConstantsMocks;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class BusinessHourTimeRangeValidatorTest {

    private BusinessHourTimeRangeValidator businessHourTimeRangeValidator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        businessHourTimeRangeValidator = new BusinessHourTimeRangeValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void shouldReturnTrueWhenOpenTimeIsBeforeCloseTime() {
        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt(ConstantsMocks.TIME_08h00m00)
                .closeAt(ConstantsMocks.TIME_18h00m00)
                .build();
        Assertions.assertTrue(businessHourTimeRangeValidator.isValid(dto, context));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidBusinessHourDTOs")
    void shouldReturnFalseForInvalidDTOs(BusinessHourDTORequest dto) {
        Assertions.assertFalse(businessHourTimeRangeValidator.isValid(dto, context));
    }

    private static Stream<Arguments> provideInvalidBusinessHourDTOs() {
        return Stream.of(
                Arguments.of((BusinessHourDTORequest) null),
                Arguments.of(BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openAt(null)
                        .closeAt(ConstantsMocks.TIME_18h00m00)
                        .build()),
                Arguments.of(BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openAt(ConstantsMocks.TIME_08h00m00)
                        .closeAt(null)
                        .build()),
                Arguments.of(BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openAt(ConstantsMocks.TIME_INVALID)
                        .closeAt(ConstantsMocks.TIME_18h00m00)
                        .build()),
                Arguments.of(BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openAt(ConstantsMocks.TIME_18h00m00)
                        .closeAt(ConstantsMocks.TIME_08h00m00)
                        .build()),
                Arguments.of(BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openAt(ConstantsMocks.TIME_10h00m00)
                        .closeAt(ConstantsMocks.TIME_10h00m00)
                        .build())
        );
    }

}