package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.api.domain.request.BusinessHourDTORequest;
import com.ppm.delivery.seller.api.service.api.validation.businessHour.BusinessHourTimeRangeValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class BusinessHourTimeRangeValidatorTest {

    private BusinessHourTimeRangeValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new BusinessHourTimeRangeValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void shouldReturnFalseWhenDTOIsNull() {
        Assertions.assertFalse(validator.isValid(null, context));
    }

    @Test
    void shouldReturnFalseWhenOpenOrCloseIsNull() {
        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt(null)
                .closeAt("18:00:00")
                .build();
        Assertions.assertFalse(validator.isValid(dto, context));

        BusinessHourDTORequest dto2 = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("08:00:00")
                .closeAt(null)
                .build();
        Assertions.assertFalse(validator.isValid(dto2, context));
    }

    @Test
    void shouldReturnFalseWhenTimeFormatIsInvalid() {
        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("invalid")
                .closeAt("18:00:00")
                .build();

        Assertions.assertFalse(validator.isValid(dto, context));
    }

    @Test
    void shouldReturnTrueWhenOpenTimeIsBeforeCloseTime() {
        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("08:00:00")
                .closeAt("18:00:00")
                .build();
        Assertions.assertTrue(validator.isValid(dto, context));
    }

    @Test
    void shouldReturnFalseWhenOpenTimeIsAfterOrEqualToCloseTime() {
        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("18:00:00")
                .closeAt("08:00:00")
                .build();
        Assertions.assertFalse(validator.isValid(dto, context), "OpenAt 18:00:00 should not be valid when CloseAt is 08:00:00");

        BusinessHourDTORequest dto2 = BusinessHourDTORequest.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openAt("10:00:00")
                .closeAt("10:00:00")
                .build();
        Assertions.assertFalse(validator.isValid(dto2, context), "OpenAt and CloseAt should not be equal");
    }

}