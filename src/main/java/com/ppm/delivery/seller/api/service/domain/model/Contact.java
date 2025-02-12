package com.ppm.delivery.seller.api.service.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Contact {
    private Long id;
    private String type;
    private String value;

}
