package com.ppm.delivery.seller.api.service.api.domain.request;

import com.ppm.delivery.seller.api.service.api.validation.businessHour.ValidBusinessHourTimeRange;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;

@Getter
@Setter
@Builder
@ValidBusinessHourTimeRange
public class BusinessHourDTORequest {

        @NotNull(message = MessageErrorConstants.ERROR_DAY_OF_WEEK_MUST_BE_PROVIDED)
        DayOfWeek dayOfWeek;

        @Pattern(
                regexp = "^([01]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$",
                message = "Invalid opening time. Times must be in 24-hour format, i.e., HH:mm:ss (e.g., 08:00:00 or 23:59:00)."
        )
        @NotNull(message = MessageErrorConstants.ERROR_OPEN_TIME_MUST_BE_PROVIDED)
        String openAt;

        @Pattern(
                regexp = "^([01]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$",
                message = "Invalid closing time. Times must be in 24-hour format, i.e., HH:mm:ss (e.g., 08:00:00 or 23:59:00)."
        )
        @NotNull(message = MessageErrorConstants.ERROR_CLOSE_TIME_MUST_BE_PROVIDED)
        String closeAt;
}