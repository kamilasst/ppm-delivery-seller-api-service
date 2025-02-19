package com.ppm.delivery.seller.api.service.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "value", nullable = false, length = 100)
    private String value;

    @ManyToOne
    @JoinColumn(name = "seller_code", referencedColumnName = "code", nullable = false)
    private Seller seller;

}
