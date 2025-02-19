package com.ppm.delivery.seller.api.service.domain.model;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Audit {

    @Column(name = "created_at", nullable = false, updatable = false)
    private String createAt;
    @Column(name = "updated_at")
    private String updateAt;

}
