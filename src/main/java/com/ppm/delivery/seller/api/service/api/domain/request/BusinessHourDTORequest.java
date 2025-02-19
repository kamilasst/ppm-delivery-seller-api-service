package com.ppm.delivery.seller.api.service.api.domain.request;

public record BusinessHourDTORequest(
        String dayOfWeek,
        String openAt,
        String closeAt

) {
}
