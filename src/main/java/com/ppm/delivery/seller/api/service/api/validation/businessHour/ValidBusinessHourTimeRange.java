package com.ppm.delivery.seller.api.service.api.validation.businessHour;

import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BusinessHourTimeRangeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBusinessHourTimeRange {
    String message() default MessageErrorConstants.ERROR_OPENING_TIME_MUST_BE_BEFORE_CLOSING_TIME;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}