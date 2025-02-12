package com.ppm.delivery.seller.api.service.api.domain.request;

import jakarta.validation.constraints.NotNull;

public record AddressDTORequest(
        @NotNull(message = "Location is required")
        LocationDTORequest location
) {
}

