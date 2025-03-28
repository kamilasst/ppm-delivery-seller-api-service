package com.ppm.delivery.seller.api.service.api.domain.request;

import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import jakarta.validation.Valid;
import lombok.Builder;

import java.util.List;

@Builder
public record SellerUpdateDTORequest(
        Status status,

        @Valid
        List<BusinessHourDTORequest> businessHours

) {
}