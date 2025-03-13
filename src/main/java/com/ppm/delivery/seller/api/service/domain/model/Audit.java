package com.ppm.delivery.seller.api.service.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Audit {

    @NotNull(message = "Create date cannot be null")
    private String createAt;

    private String updateAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Audit audit = (Audit) o;
        return Objects.equals(createAt, audit.createAt) &&
                Objects.equals(updateAt, audit.updateAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createAt, updateAt);
    }

}
