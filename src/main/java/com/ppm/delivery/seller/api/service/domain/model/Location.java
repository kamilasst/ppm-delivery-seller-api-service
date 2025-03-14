package com.ppm.delivery.seller.api.service.domain.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Builder
@ToString
public class Location {

    @Embedded
    @AttributeOverride(name = "latitude", column = @Column(name = "location_latitude"))
    @AttributeOverride(name = "longitude", column = @Column(name = "location_longitude"))
    private GeoCoordinates geoCoordinates;

    @Column(name = "city", length = 100, nullable = false)
    private String city;

    @Column(name = "country", length = 100, nullable = false)
    private String country;

    @Column(name = "state", length = 100, nullable = false)
    private String state;

    @Column(name = "number", length = 10, nullable = false)
    private String number;

    @Column(name = "zip_code", length = 10, nullable = false)
    private String zipCode;

    @Column(name = "street_address", length = 255, nullable = false)
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
