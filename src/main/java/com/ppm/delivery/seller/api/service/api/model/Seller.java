package com.ppm.delivery.seller.api.service.api.model;



import com.ppm.delivery.seller.api.service.api.model.enums.Status;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Seller {

    private Integer id;
    private String code;
    private CodeIdentification codeIdentification;
    private String name;
    private String displayName;
    private List<Contact> contacts;
    private Address address;
    private String creatorId;
    private Status status;
    private Map<String, List<BusinessHour>> businessHours;
    private Audit audit;

}

