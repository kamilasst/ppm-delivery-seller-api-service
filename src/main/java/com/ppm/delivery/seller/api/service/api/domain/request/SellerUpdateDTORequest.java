package com.ppm.delivery.seller.api.service.api.domain.request;

import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.Optional;

@Builder
public record SellerUpdateDTORequest(
        Optional<Status> status,

        Optional<
                @NotNull(message = "Business hours cannot be null")
                        List<BusinessHourDTORequest>
                > businessHours
) {
}