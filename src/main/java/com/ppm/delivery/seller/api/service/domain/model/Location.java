package com.ppm.delivery.seller.api.service.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Location {

    @NotNull(message = "GeoCOordinates cannot be null")
    private GeoCoordinates geoCoordinates;

    @NotNull(message = "City is required")
    private String city;

    @NotNull(message = "Country is required")
    private String country;

    @NotNull(message = "State is required")
    private String state;

    @NotNull(message = "Number is required")
    private String number;

    @NotNull(message = "Zip Code is required")
    private String zipCode;

    @NotNull(message = "Street Address is required")
    private String streetAddress;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(geoCoordinates, location.geoCoordinates) &&
                Objects.equals(city, location.city) &&
                Objects.equals(country, location.country) &&
                Objects.equals(state, location.state) &&
                Objects.equals(number, location.number) &&
                Objects.equals(zipCode, location.zipCode) &&
                Objects.equals(streetAddress, location.streetAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(geoCoordinates, city, country, state, number, zipCode, streetAddress);
    }

}
