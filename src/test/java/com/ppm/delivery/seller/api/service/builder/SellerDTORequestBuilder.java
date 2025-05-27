package com.ppm.delivery.seller.api.service.builder;

import com.ppm.delivery.seller.api.service.api.domain.request.*;
import com.ppm.delivery.seller.api.service.constants.ConstantsMocks;
import com.ppm.delivery.seller.api.service.constants.SellerMockConstants;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SellerDTORequestBuilder {

    private SellerDTORequestBuilder() {
    }

    private static SellerDTORequest create(List<BusinessHourDTORequest> businessHours) {
        return SellerDTORequest.builder()
                .identification(IdentificationDTORequest.builder()
                        .type(SellerMockConstants.DEFAULT_IDENTIFICATION_TYPE)
                        .code(SellerMockConstants.DEFAULT_IDENTIFICATION_CODE)
                        .build())
                .name(SellerMockConstants.DEFAULT_NAME)
                .displayName(SellerMockConstants.DEFAULT_DISPLAY_NAME)
                .contacts(List.of(ContactDTORequest.builder()
                        .type(SellerMockConstants.DEFAULT_CONTACT_TYPE)
                        .value(SellerMockConstants.DEFAULT_CONTACT_VALUE)
                        .build()))
                .address(AddressDTORequest.builder()
                        .location(LocationDTORequest.builder()
                                .geoCoordinates(GeoCoordinatesDTORequest.builder()
                                        .latitude(SellerMockConstants.DEFAULT_LATITUDE)
                                        .longitude(SellerMockConstants.DEFAULT_LONGITUDE)
                                        .build())
                                .city(SellerMockConstants.DEFAULT_CITY)
                                .country(SellerMockConstants.DEFAULT_COUNTRY)
                                .state(SellerMockConstants.DEFAULT_STATE)
                                .number(SellerMockConstants.DEFAULT_NUMBER)
                                .zipCode(SellerMockConstants.DEFAULT_ZIP_CODE)
                                .streetAddress(SellerMockConstants.DEFAULT_STREET)
                                .build())
                        .build())
                .creatorId(SellerMockConstants.DEFAULT_CREATOR_ID)
                .businessHours(businessHours)
                .build();
    }

    public static SellerDTORequest createDefault() {
        return create(new ArrayList<>(List.of(
                BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.SUNDAY)
                        .openAt(ConstantsMocks.TIME_00h00m00)
                        .closeAt(ConstantsMocks.TIME_23h59m00)
                        .build()
        )));

    }

    public static SellerDTORequest createWithNullBusinessHours() {
        return create(null);
    }

    public static SellerDTORequest createWithEmptyBusinessHours() {
        return create(new ArrayList<>());
    }

    public static SellerDTORequest createWithoutBusinessHours() {
        return SellerDTORequest.builder()
                .identification(IdentificationDTORequest.builder()
                        .type(SellerMockConstants.DEFAULT_IDENTIFICATION_TYPE)
                        .code(SellerMockConstants.DEFAULT_IDENTIFICATION_CODE)
                        .build())
                .name(SellerMockConstants.DEFAULT_NAME)
                .displayName(SellerMockConstants.DEFAULT_DISPLAY_NAME)
                .contacts(List.of(ContactDTORequest.builder()
                        .type(SellerMockConstants.DEFAULT_CONTACT_TYPE)
                        .value(SellerMockConstants.DEFAULT_CONTACT_VALUE)
                        .build()))
                .address(AddressDTORequest.builder()
                        .location(LocationDTORequest.builder()
                                .geoCoordinates(GeoCoordinatesDTORequest.builder()
                                        .latitude(SellerMockConstants.DEFAULT_LATITUDE)
                                        .longitude(SellerMockConstants.DEFAULT_LONGITUDE)
                                        .build())
                                .city(SellerMockConstants.DEFAULT_CITY)
                                .country(SellerMockConstants.DEFAULT_COUNTRY)
                                .state(SellerMockConstants.DEFAULT_STATE)
                                .number(SellerMockConstants.DEFAULT_NUMBER)
                                .zipCode(SellerMockConstants.DEFAULT_ZIP_CODE)
                                .streetAddress(SellerMockConstants.DEFAULT_STREET)
                                .build())
                        .build())
                .creatorId(SellerMockConstants.DEFAULT_CREATOR_ID)
                .build();
    }

    public static SellerDTORequest createWithCustomGeoCoordinates(SellerDTORequest original, GeoCoordinatesDTORequest geoCoordinates) {
        return SellerDTORequest.builder()
                .identification(original.identification())
                .name(original.name())
                .displayName(original.displayName())
                .contacts(original.contacts())
                .address(AddressDTORequest.builder()
                        .location(LocationDTORequest.builder()
                                .geoCoordinates(geoCoordinates)
                                .city(original.address().location().city())
                                .country(original.address().location().country())
                                .state(original.address().location().state())
                                .number(original.address().location().number())
                                .zipCode(original.address().location().zipCode())
                                .streetAddress(original.address().location().streetAddress())
                                .build())
                        .build())
                .creatorId(original.creatorId())
                .businessHours(original.businessHours())
                .build();
    }

}
