package com.ppm.delivery.seller.api.service.builder;

import com.ppm.delivery.seller.api.service.api.domain.request.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SellerDTORequestBuilder {

    private SellerDTORequestBuilder() {
    }

    public static SellerDTORequest createDefault() {
        return SellerDTORequest.builder()
                .identification(IdentificationDTORequest.builder()
                        .type("CNPJ")
                        .code("12345678901435")
                        .build())
                .name("Bar do Cuscuz LTDA")
                .displayName("Bar do Cuscuz")
                .contacts(List.of(ContactDTORequest.builder()
                        .type("MAIL")
                        .value("+pTryOZ7hrzjbfz4OuXQ4g==")
                        .build()))
                .address(AddressDTORequest.builder()
                        .location(LocationDTORequest.builder()
                                .geoCoordinates(GeoCoordinatesDTORequest.builder()
                                        .latitude("-23.520930238344484")
                                        .longitude("-46.905673295259476")
                                        .build())
                                .city("-46.905673295259476")
                                .country("BR")
                                .state("São Paulo")
                                .number("2161")
                                .zipCode("51021-200")
                                .streetAddress("Avenida Henrique Gonçalves Baptista")
                                .build())
                        .build())
                .creatorId("d41f2c7b-c04e-4c2a-b084-8bec13261637")
                .businessHours(new ArrayList<>(Arrays.asList(BusinessHourDTORequest.builder()
                        .dayOfWeek(DayOfWeek.SUNDAY)
                        .openAt("00:00:00")
                        .closeAt("23:59:00")
                        .build())))
                .build();
    }

    public static SellerDTORequest createWithNullBusinessHours() {
        return SellerDTORequest.builder()
                .identification(IdentificationDTORequest.builder()
                        .type("CNPJ")
                        .code("12345678901435")
                        .build())
                .name("Bar do Cuscuz LTDA")
                .displayName("Bar do Cuscuz")
                .contacts(List.of(ContactDTORequest.builder()
                        .type("MAIL")
                        .value("+pTryOZ7hrzjbfz4OuXQ4g==")
                        .build()))
                .address(AddressDTORequest.builder()
                        .location(LocationDTORequest.builder()
                                .geoCoordinates(GeoCoordinatesDTORequest.builder()
                                        .latitude("-23.520930238344484")
                                        .longitude("-46.905673295259476")
                                        .build())
                                .city("-46.905673295259476")
                                .country("BR")
                                .state("São Paulo")
                                .number("2161")
                                .zipCode("51021-200")
                                .streetAddress("Avenida Henrique Gonçalves Baptista")
                                .build())
                        .build())
                .creatorId("d41f2c7b-c04e-4c2a-b084-8bec13261637")
                .businessHours(null)
                .build();
    }

    public static SellerDTORequest createWithEmptyBusinessHours() {
        return SellerDTORequest.builder()
                .identification(IdentificationDTORequest.builder()
                        .type("CNPJ")
                        .code("12345678901435")
                        .build())
                .name("Bar do Cuscuz LTDA")
                .displayName("Bar do Cuscuz")
                .contacts(List.of(ContactDTORequest.builder()
                        .type("MAIL")
                        .value("+pTryOZ7hrzjbfz4OuXQ4g==")
                        .build()))
                .address(AddressDTORequest.builder()
                        .location(LocationDTORequest.builder()
                                .geoCoordinates(GeoCoordinatesDTORequest.builder()
                                        .latitude("-23.520930238344484")
                                        .longitude("-46.905673295259476")
                                        .build())
                                .city("-46.905673295259476")
                                .country("BR")
                                .state("São Paulo")
                                .number("2161")
                                .zipCode("51021-200")
                                .streetAddress("Avenida Henrique Gonçalves Baptista")
                                .build())
                        .build())
                .creatorId("d41f2c7b-c04e-4c2a-b084-8bec13261637")
                .businessHours(Collections.emptyList())
                .build();
    }

    public static SellerDTORequest createWithoutBusinessHours() {
        return SellerDTORequest.builder()
                .identification(IdentificationDTORequest.builder()
                        .type("CNPJ")
                        .code("12345678901459")
                        .build())
                .name("Bar do Cuscuz LTDA")
                .displayName("Bar do Cuscuz")
                .contacts(List.of(ContactDTORequest.builder()
                        .type("MAIL")
                        .value("+pTryOZ7hrzjbfz4OuXQ4g==")
                        .build()))
                .address(AddressDTORequest.builder()
                        .location(LocationDTORequest.builder()
                                .geoCoordinates(GeoCoordinatesDTORequest.builder()
                                        .latitude("-23.520930238344484")
                                        .longitude("-46.905673295259476")
                                        .build())
                                .city("Barueri")
                                .country("BR")
                                .state("São Paulo")
                                .number("2161")
                                .zipCode("51021-200")
                                .streetAddress("Avenida Henrique Gonçalves Baptista")
                                .build())
                        .build())
                .creatorId("d41f2c7b-c04e-4c2a-b084-8bec13261637")
                .build();
    }

}
