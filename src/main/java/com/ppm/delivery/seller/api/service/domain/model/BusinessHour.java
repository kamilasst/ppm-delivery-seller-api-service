package com.ppm.delivery.seller.api.service.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "business_hour")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BusinessHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;

    @Column(name = "open_at", nullable = false)
    private String openAt;

    @Column(name = "close_at", nullable = false)
    private String closeAt;

    @ManyToOne
    @JoinColumn(name = "seller_code", referencedColumnName = "code", nullable = false)
    private Seller seller;

}
