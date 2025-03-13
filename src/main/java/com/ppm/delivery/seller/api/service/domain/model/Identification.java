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
public class Identification {

    @NotNull(message = "Identification type is required")
    @Size(max = 50, message = "Identification type cannot be longer than 50 characters")
    private String type;

    @NotNull(message = "Identification code is required")
    @Size(min = 14, max = 14, message = "Identification code must be exactly 14 characters")
    private String code;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identification that = (Identification) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, code);
    }

}
