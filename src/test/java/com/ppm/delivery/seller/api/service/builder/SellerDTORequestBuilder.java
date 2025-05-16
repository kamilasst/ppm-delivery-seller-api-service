package com.ppm.delivery.seller.api.service.builder;

import com.ppm.delivery.seller.api.service.api.domain.request.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// TODO atg ReviewCode: Por favor avalie criar constants para os valores
public final class SellerDTORequestBuilder {

    private SellerDTORequestBuilder() {
    }

    // TODO atg ReviewCode: Os metodos estao muito iguals, por favor avalie uma forma de reutilizar o código: exemplo:
//    private static SellerDTORequest create(String identificationType, String identificationCode, String name, String displayName, String contactType, String contactValue, String countryCode, String state, String city, String streetAddress, String number, String zipCode) {
//        return SellerDTORequest.builder()
//                .identification(IdentificationDTORequest.builder()
//                        .type(identificationType)
//                        .code(identificationCode)
//                        .build())
//                .name(name)
//                .displayName(displayName)
//                ...
//    }

// e dentro dos metodos createDefault, createWithNullBusinessHours e etc vc pode chamar ele passando os parametros necesarios
//    public static SellerDTORequest createDefault() {
//        return create(IDENTIFICATION_CODE_CNPJ, IDENTIFICATION_CODE_CNPJ_VALUE,);
//                NAME, DISPLAY_NAME, CONTACT_TYPE, CONTACT_VALUE,
//                COUNTRY_CODE, STATE, CITY, STREET_ADDRESS, NUMBER, ZIP_CODE);
//    }
//    public static SellerDTORequest createWithNullBusinessHours() {
//        return create(PASSAR NULO PARA BUSINESS HOURS);
//    }


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
                                        .latitude(-23.520930238344484)
                                        .longitude(-46.905673295259476)
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
                                        .latitude(-23.520930238344484)
                                        .longitude(-46.905673295259476)
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
                                        .latitude(-23.520930238344484)
                                        .longitude(-46.905673295259476)
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
                                        .latitude(-23.520930238344484)
                                        .longitude(-46.905673295259476)
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
