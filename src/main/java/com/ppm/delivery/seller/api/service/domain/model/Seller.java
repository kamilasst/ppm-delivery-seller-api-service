package com.ppm.delivery.seller.api.service.domain.model;

import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "seller")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Embedded
    private IdentificationCode identificationCode;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Contact> contacts = new ArrayList<>();

    @Embedded
    private Address address;

    @Column(name = "creator_id", nullable = false, length = 100)
    private String creatorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BusinessHour> businessHours = new ArrayList<>();

    @Embedded
    private Audit audit;

}

