package com.ppm.delivery.seller.api.service.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Audit {
    private String createAt;
    private String updateAt;

}
