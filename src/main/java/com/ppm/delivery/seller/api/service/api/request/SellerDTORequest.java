package com.ppm.delivery.seller.api.service.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

public record SellerDTORequest(

        @NotBlank(message = "Code is required")
        String code,

        @Valid
        @NotNull(message = "CodeIdentification is required")
        CodeIdentificationDTORequest codeIdentification,

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "DisplayName is required")
        @Size(min = 2, max = 100, message = "DisplayName must be between 2 and 100 characters")
        String displayName,

        @NotNull(message = "Contacts are required")
        @Valid
        @Size(min = 1, message = "At least one contact is required")
        List<ContactDTORequest> contacts,

        @Valid
        @NotNull(message = "Address is required")
        AddressDTORequest address,

        @NotBlank(message = "CreatorId is required")
        String creatorId,
        Map<String, BusinessHourDTORequest> businessHours

) {
}










