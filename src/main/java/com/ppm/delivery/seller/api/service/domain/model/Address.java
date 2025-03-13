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
public class Address {

    @NotNull(message = "Location cannot be null")
    private Location location;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(location, address.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }

}
