package com.ppm.delivery.seller.api.service.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContactDTORequest(

        @NotBlank(message = "Type is required")
        String type,

        @NotBlank(message = "Value is required")
        String value

) {
}
