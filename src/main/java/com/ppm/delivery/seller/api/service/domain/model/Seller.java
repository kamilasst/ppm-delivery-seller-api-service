package com.ppm.delivery.seller.api.service.domain.model;

import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String code;
    @Transient
    private CodeIdentification codeIdentification;
    private String name;
    private String displayName;
    @Transient
    private List<Contact> contacts;
    @Transient
    private Address address;
    private String creatorId;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Transient
    private Map<String, List<BusinessHour>> businessHours;
    @Transient
    private Audit audit;

}

