package com.ppm.delivery.seller.api.service.domain.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;
import java.util.StringJoiner;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {

    private Long id;

    @NotNull(message = "Type cannot be null")
    @Size(min = 2, max = 50, message = "Type must be between 2 and 50 characters")
    private String type;

    @NotNull(message = "Value cannot be null")
    @Size(min = 1, max = 100, message = "Value must be between 1 and 100 characters")
    private String value;

    @NotNull(message = "Seller cannot be null")
    private Seller seller;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(type, contact.type) &&
                Objects.equals(value, contact.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Contact.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("type='" + type + "'")
                .add("value='" + value + "'")
                .toString();
    }

}
