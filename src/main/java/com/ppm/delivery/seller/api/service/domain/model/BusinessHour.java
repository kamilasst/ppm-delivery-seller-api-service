package com.ppm.delivery.seller.api.service.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BusinessHour {
    private String openAt;
    private String closeAt;

}
