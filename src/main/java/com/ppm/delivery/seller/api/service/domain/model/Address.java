package com.ppm.delivery.seller.api.service.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Embeddable
public class Address {

    @Embedded
    private Location location;

}
