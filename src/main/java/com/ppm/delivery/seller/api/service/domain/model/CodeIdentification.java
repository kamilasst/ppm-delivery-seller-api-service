package com.ppm.delivery.seller.api.service.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Embeddable
public class CodeIdentification {

    @Column(name = "identification_type", nullable = false)
    private String type;
    @Column(name = "identification_code", nullable = false, unique = true)
    private String code;

}
