package com.ppm.delivery.seller.api.service.api.domain.request;

import lombok.Builder;

@Builder
public record BusinessHourDTORequest(
        String dayOfWeek,
        String openAt,
        String closeAt

) {
}
