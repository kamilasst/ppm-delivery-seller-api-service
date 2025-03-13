package com.ppm.delivery.seller.api.service.domain.model;

import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Seller {

    private Integer id;

    @NotNull(message = "Code is required")
    @Size(max = 255, message = "Code cannot be longer than 255 characters")
    private String code;

    @Valid
    private Identification identification;

    @NotNull(message = "Name is required")
    @Size(max = 100, message = "Name cannot be longer than 100 characters")
    private String name;

    @NotNull(message = "Display name is required")
    private String displayName;

    @Valid
    private List<Contact> contacts = new ArrayList<>();

    @Valid
    private Address address;

    @NotNull(message = "Creator ID is required")
    @Size(max = 255, message = "Creator ID cannot be longer than 100 characters")
    private String creatorId;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Valid
    private List<BusinessHour> businessHours = new ArrayList<>();

    @Valid
    private Audit audit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seller seller = (Seller) o;
        return Objects.equals(code, seller.code) &&
                Objects.equals(identification, seller.identification) &&
                Objects.equals(name, seller.name) &&
                Objects.equals(displayName, seller.displayName) &&
                Objects.equals(new HashSet<>(contacts), new HashSet<>(seller.contacts)) &&
                Objects.equals(address, seller.address) &&
                Objects.equals(creatorId, seller.creatorId) &&
                Objects.equals(status, seller.status) &&
                Objects.equals(new HashSet<>(businessHours), new HashSet<>(seller.businessHours));
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, identification, name, displayName, contacts, address, creatorId, status, businessHours);
    }

}

