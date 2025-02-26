package com.ppm.delivery.seller.api.service.api.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record GeoCoordinatesDTORequest(
        @NotBlank(message = "Latitude is required")
        String latitude,
        @NotBlank(message = "Longitude is required")
        String longitude

) {
}
