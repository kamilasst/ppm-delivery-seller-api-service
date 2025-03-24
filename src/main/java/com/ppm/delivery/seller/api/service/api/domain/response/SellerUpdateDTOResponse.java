package com.ppm.delivery.seller.api.service.api.domain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SellerUpdateDTOResponse(

        String code,
        Status status,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime updatedDate

) {
}
