package com.ppm.delivery.seller.api.service.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CodeIdentificationDTORequest(
        @NotBlank(message = "Type is required")
        String type,

        @NotBlank(message = "Code is required")
        String code

) {
}
