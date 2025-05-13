package com.ppm.delivery.seller.api.service.api.validation.businessHour;

import com.ppm.delivery.seller.api.service.api.domain.request.BusinessHourDTORequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class BusinessHourTimeRangeValidator implements ConstraintValidator<ValidBusinessHourTimeRange, BusinessHourDTORequest> {

    @Override
    public boolean isValid(BusinessHourDTORequest value, ConstraintValidatorContext context) {
        if (value == null || value.getOpenAt() == null || value.getCloseAt() == null) {
            return false;
        }

        try {
            LocalTime open = LocalTime.parse(value.getOpenAt());
            LocalTime close = LocalTime.parse(value.getCloseAt());

            return open.isBefore(close);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}