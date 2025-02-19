package com.ppm.delivery.seller.api.service.domain.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Embeddable
public class Location {

    @Embedded
    @AttributeOverride(name = "latitude", column = @Column(name = "location_latitude"))
    @AttributeOverride(name = "longitude", column = @Column(name = "location_longitude"))
    private GeoCoordinates geoCoordinates;

    @Column(name = "city", length = 100, nullable = false)
    @NotNull(message = "City is required")
    private String city;

    @Column(name = "country", length = 100, nullable = false)
    @NotNull(message = "Country is required")
    private String country;

    @Column(name = "state", length = 100, nullable = false)
    @NotNull(message = "State is required")
    private String state;

    @Column(name = "number", length = 10, nullable = false)
    @NotNull(message = "Number is required")
    private String number;

    @Column(name = "zip_code", length = 10, nullable = false)
    @NotNull(message = "Zip Code is required")
    private String zipCode;

    @Column(name = "street_address", length = 255, nullable = false)
    @NotNull(message = "Street Address is required")
    private String streetAddress;

}
