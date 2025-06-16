package com.ppm.delivery.seller.api.service.api.domain.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record SellerNearSearchRequest(

        @NotNull(message = "orderCreateDate is required")
        LocalDateTime orderCreateDate,

        @NotNull(message = "orderDeliveryInfo is required")
        DeliveryInfoDTO orderDeliveryInfo,

        @NotNull(message = "radius is required")
        @Min(value = 1, message = "Radius must be at least 1 meter")
        @DecimalMax(value = "50000.0", message = "Radius cannot exceed 50000 meters")
        Double radius,

        List<String> projections
) {
    public record DeliveryInfoDTO(
            @NotNull(message = "latitude is required")
            @DecimalMin(value = "-90.0", message = "Latitude must be >= -90.0")
            @DecimalMax(value = "90.0", message = "Latitude must be <= 90.0")
            Double latitude,

            @NotNull(message = "longitude is required")
            @DecimalMin(value = "-180.0", message = "Longitude must be >= -180.0")
            @DecimalMax(value = "180.0", message = "Longitude must be <= 180.0")
            Double longitude
    ) {
    }
}