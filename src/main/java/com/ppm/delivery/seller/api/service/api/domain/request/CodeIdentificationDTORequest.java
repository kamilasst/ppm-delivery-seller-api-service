package com.ppm.delivery.seller.api.service.api.domain.request;

import jakarta.validation.constraints.NotBlank;

public record CodeIdentificationDTORequest(
        @NotBlank(message = "Type is required")
        String type,
        @NotBlank(message = "Code is required")
        String code

) {
}
