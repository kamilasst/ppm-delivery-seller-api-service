package com.ppm.delivery.seller.api.service.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessHour that = (BusinessHour) o;
        return Objects.equals(dayOfWeek, that.dayOfWeek) &&
                Objects.equals(openAt, that.openAt) &&
                Objects.equals(closeAt, that.closeAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, openAt, closeAt);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BusinessHour.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("dayOfWeek='" + dayOfWeek + "'")
                .add("openAt='" + openAt + "'")
                .add("closeAt='" + closeAt + "'")
                .toString();
    }

}
