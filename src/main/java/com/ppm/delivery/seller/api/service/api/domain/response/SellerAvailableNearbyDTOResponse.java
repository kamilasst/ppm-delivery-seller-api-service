package com.ppm.delivery.seller.api.service.api.domain.response;

import com.ppm.delivery.seller.api.service.api.domain.request.AddressDTORequest;
import com.ppm.delivery.seller.api.service.domain.model.Identification;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class SellerAvailableNearbyDTOResponse {

    private String code;
    private String countryCode;
    private Identification identification;
    private String name;
    private String displayName;
    private List<ContactDTO> contacts;
    private AddressDTORequest address;
    private Status status;
    private List<BusinessHourDTO> businessHours;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @EqualsAndHashCode
    public static class ContactDTO {
        private String type;
        private String value;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @EqualsAndHashCode
    public static class BusinessHourDTO {
        private String dayOfWeek;
        private String openAt;
        private String closeAt;
    }

}
