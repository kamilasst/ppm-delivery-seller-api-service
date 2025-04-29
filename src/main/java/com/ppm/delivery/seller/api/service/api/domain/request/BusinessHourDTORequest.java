package com.ppm.delivery.seller.api.service.api.domain.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record BusinessHourDTORequest(

        String dayOfWeek,
        @Pattern(
                regexp = "^([01]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$",
                message = "Invalid opening time. Times must be in 24-hour format, i.e., HH:mm:ss (e.g., 08:00:00 or 23:59:00)."
        )
        String openAt,
        @Pattern(
                regexp = "^([01]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$",
                message = "Invalid closing time. Times must be in 24-hour format, i.e., HH:mm:ss (e.g., 08:00:00 or 23:59:00)."
        )
        String closeAt

) {
}
