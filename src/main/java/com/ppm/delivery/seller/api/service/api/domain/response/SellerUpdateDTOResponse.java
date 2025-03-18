package com.ppm.delivery.seller.api.service.api.domain.response;

import com.ppm.delivery.seller.api.service.api.domain.request.BusinessHourDTORequest;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import lombok.Builder;

import java.util.List;

@Builder
public record SellerUpdateDTOResponse(

        String code,
        String updateDate,
        Status status,
        List<BusinessHourDTORequest> businessHours

) {
}
