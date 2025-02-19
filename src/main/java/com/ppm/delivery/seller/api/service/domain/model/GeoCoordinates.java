package com.ppm.delivery.seller.api.service.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Embeddable
public class GeoCoordinates {

    @Column(name = "latitude", length = 50)
    private String latitude;
    @Column(name = "longitude", length = 50)
    private String longitude;

}
