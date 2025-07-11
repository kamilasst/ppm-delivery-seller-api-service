package com.ppm.delivery.seller.api.service.domain.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Seller {

    @Id
    private String code;

    @Column(name = "country_code", nullable = false, length = 10)
    private String countryCode;

    @Embedded
    private Identification identification;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Contact> contacts = new ArrayList<>();

    @Embedded
    private Address address;

    @Column(name = "creator_id", nullable = false, length = 100)
    private String creatorId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<BusinessHour> businessHours;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seller seller = (Seller) o;

        return Objects.equals(code, seller.code) &&
                Objects.equals(countryCode, seller.countryCode) &&
                Objects.equals(identification, seller.identification) &&
                Objects.equals(name, seller.name) &&
                Objects.equals(displayName, seller.displayName) &&
                Objects.equals(new HashSet<>(safe(contacts)), new HashSet<>(safe(seller.contacts))) &&
                Objects.equals(address, seller.address) &&
                Objects.equals(creatorId, seller.creatorId) &&
                Objects.equals(status, seller.status) &&
                Objects.equals(new HashSet<>(safe(businessHours)), new HashSet<>(safe(seller.businessHours)));
    }

    private <T> Collection<T> safe(Collection<T> collection) {
        return collection == null ? Collections.emptySet() : collection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                code,
                countryCode,
                identification,
                name,
                displayName,
                new HashSet<>(safe(contacts)),
                address,
                creatorId,
                status,
                new HashSet<>(safe(businessHours))
        );
    }

}

