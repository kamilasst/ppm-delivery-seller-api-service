package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.api.domain.request.BusinessHourDTORequest;
import com.ppm.delivery.seller.api.service.api.validation.businessHour.BusinessHourTimeRangeValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
        assertFalse(validator.isValid(null, context));
    }

    @Test
    void shouldReturnFalseWhenOpenOrCloseIsNull() {
        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek("MONDAY")
                .openAt(null)
                .closeAt("18:00:00")
                .build();
        assertFalse(validator.isValid(dto, context));

        BusinessHourDTORequest dto2 = BusinessHourDTORequest.builder()
                .dayOfWeek("MONDAY")
                .openAt("08:00:00")
                .closeAt(null)
                .build();
        assertFalse(validator.isValid(dto2, context));
    }

    @Test
    void shouldReturnFalseWhenTimeFormatIsInvalid() {
        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek("MONDAY")
                .openAt("invalid")
                .closeAt("18:00:00")
                .build();

        assertFalse(validator.isValid(dto, context));
    }

    @Test
    void shouldReturnTrueWhenOpenTimeIsBeforeCloseTime() {
        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek("MONDAY")
                .openAt("08:00:00")
                .closeAt("18:00:00")
                .build();
        assertTrue(validator.isValid(dto, context));
    }

    @Test
    void shouldReturnFalseWhenOpenTimeIsAfterOrEqualToCloseTime() {
        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek("MONDAY")
                .openAt("18:00:00")
                .closeAt("08:00:00")
                .build();
        assertFalse(validator.isValid(dto, context));

        BusinessHourDTORequest dto2 = BusinessHourDTORequest.builder()
                .dayOfWeek("MONDAY")
                .openAt("10:00:00")
                .closeAt("10:00:00")
                .build();
        assertFalse(validator.isValid(dto2, context));
    }

    @Test
    void shouldReturnTrueWhenOpenTimeIs23AndCloseTimeIs00() {
        BusinessHourDTORequest dto = BusinessHourDTORequest.builder()
                .dayOfWeek("MONDAY")
                .openAt("23:59:59")
                .closeAt("00:00:00")
                .build();
        assertTrue(validator.isValid(dto, context));
    }

}