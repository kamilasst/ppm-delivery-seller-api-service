package com.ppm.delivery.seller.api.service.domain.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Objects;
import java.util.StringJoiner;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessHour {

    private Integer id;

    @NotNull(message = "Day of week cannot be null")
    @Pattern(regexp = "^(sunday|monday|tuesday|wednesday|thursday|friday|saturday)$",
            message = "Day of week must be a valid day of the week (e.g., sunday, monday, etc.)")
    private String dayOfWeek;

    @NotNull(message = "Open time cannot be null")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$", message = "Open time must be in HH:mm:ss format")
    private String openAt;

    @NotNull(message = "Close time cannot be null")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$", message = "Close time must be in HH:mm:ss format")
    private String closeAt;

    @NotNull(message = "Seller cannot be null")
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
