package com.ppm.delivery.seller.api.service.api.domain.request;

import jakarta.validation.constraints.NotBlank;

public record ContactDTORequest(
        @NotBlank(message = "Type is required")
        String type,
        @NotBlank(message = "Value is required")
        String value

) {
}
