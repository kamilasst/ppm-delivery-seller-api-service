package com.ppm.delivery.seller.api.service.api.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Location {
    private GeoCoordinates geoCoordinates;
    private String city;
    private String country;
    private String state;
    private String number;
    private String zipCode;
    private String streetAddress;

}
