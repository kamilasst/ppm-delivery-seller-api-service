package com.ppm.delivery.seller.api.service.api.domain.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LocationDTORequest(
        @NotNull(message = "GeoCoordinates is required")
        @Valid
        GeoCoordinatesDTORequest geoCoordinates,
        @NotBlank(message = "City is required")
        String city,
        @NotBlank(message = "Country is required")
        String country,
        @NotBlank(message = "State is required")
        String state,
        @NotBlank(message = "Number is required")
        String number,
        @NotBlank(message = "Zip Code is required")
        String zipCode,
        @NotBlank(message = "Street Address is required")
        String streetAddress

) {
}
