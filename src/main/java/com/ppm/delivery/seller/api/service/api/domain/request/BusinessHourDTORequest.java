package com.ppm.delivery.seller.api.service.api.domain.request;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record BusinessHourDTORequest(
        String dayOfWeek,
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$", message = "Open time must be in HH:mm:ss format")
        String openAt,
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$", message = "Open time must be in HH:mm:ss format")
        String closeAt

) {
}
