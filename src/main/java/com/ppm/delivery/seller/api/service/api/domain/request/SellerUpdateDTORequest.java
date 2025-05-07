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

        @Size(min = 1, message = MessageErrorConstants.ERROR_AT_LEAST_ONE_BUSINESS_HOUR_REQUIRED)
        List<@Valid BusinessHourDTORequest> businessHours

) {
}