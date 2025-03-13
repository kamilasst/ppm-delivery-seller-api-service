package com.ppm.delivery.seller.api.service.domain.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GeoCoordinates {

    @NotNull(message = "Latitude is required")
    @Size(max = 50, message = "Latitude cannot be longer than 50 characters")
    private String latitude;

    @NotNull(message = "Longitude is required")
    @Size(max = 50, message = "Longitude cannot be longer than 50 characters")
    private String longitude;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoCoordinates that = (GeoCoordinates) o;
        return Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

}
