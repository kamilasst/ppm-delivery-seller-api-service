package com.ppm.delivery.seller.api.service.api.validation.businessHour;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BusinessHourTimeRangeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBusinessHourTimeRange {
    // TODO atg ReviewCode POST: Tem alguma forma de vc colocar esse mensagem como constante e reutilizar nos testes unitários ?
    String message() default "Opening time must be before closing time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}