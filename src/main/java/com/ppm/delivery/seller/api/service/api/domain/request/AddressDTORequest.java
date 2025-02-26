package com.ppm.delivery.seller.api.service.api.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;


@Builder
public record AddressDTORequest(
        @NotNull(message = "Location is required")
        LocationDTORequest location
) {
}

