package com.ppm.delivery.seller.api.service.api.domain.response;

import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import lombok.Builder;

@Builder
public record SellerDTOResponse(
        String code,
        Status status,
        String createDate
) {
}
