package com.ppm.delivery.seller.api.service.api.domain.request;

import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record SellerUpdateDTORequest(
        Status status,

        @Size(min = 1, message = "At least one business hour must be provided.")
        List<@Valid BusinessHourDTORequest> businessHours

) {
}