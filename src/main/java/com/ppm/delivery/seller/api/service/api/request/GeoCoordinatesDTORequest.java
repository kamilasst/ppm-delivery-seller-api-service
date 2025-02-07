package com.ppm.delivery.seller.api.service.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GeoCoordinatesDTORequest(

        @NotBlank(message = "Latitude is required")
        String latitude,

        @NotBlank(message = "Longitude is required")
        String longitude

) {
}
