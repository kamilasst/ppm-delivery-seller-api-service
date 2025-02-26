package com.ppm.delivery.seller.api.service.domain.model;

import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "seller")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Embedded
    private Identification identification;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Contact> contacts = new ArrayList<>();

    @Embedded
    private Address address;

    @Column(name = "creator_id", nullable = false, length = 100)
    private String creatorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<BusinessHour> businessHours = new ArrayList<>();

    @Embedded
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

