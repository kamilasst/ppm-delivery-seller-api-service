package com.ppm.delivery.seller.api.service.api.domain.request;

import lombok.Builder;

import java.time.DayOfWeek;

@Builder
public record BusinessHourDTORequest(
        String dayOfWeek,
        String openAt,
        String closeAt

) {
}
