package com.ppm.delivery.seller.api.service.api.response;

import com.ppm.delivery.seller.api.service.api.model.enums.Status;
import com.ppm.delivery.seller.api.service.api.request.CodeIdentificationDTORequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record SellerDTOResponse(
        String code,
        Status status,
        String createDate
) {
}
