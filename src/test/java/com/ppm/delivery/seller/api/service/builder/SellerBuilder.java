package com.ppm.delivery.seller.api.service.builder;

import com.ppm.delivery.seller.api.service.api.domain.request.*;
import com.ppm.delivery.seller.api.service.domain.model.*;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SellerBuilder {

    public static Seller createDefault(String countryCode){

        return Seller.builder()
                .code(UUID.randomUUID().toString())
                .countryCode(countryCode)
                .identification(Identification.builder()
                        .type("CNPJ")
                        .code("12345678901435")
                        .build())
                .name("Bar do Cuscuz LTDA")
                .displayName("Bar do Cuscuz")
                .contacts(List.of(Contact.builder()
                        .type("MAIL")
                        .value("+pTryOZ7hrzjbfz4OuXQ4g==")
                        .build()))
                .address(Address.builder()
                        .location(Location.builder()
                                .geoCoordinates(GeoCoordinates.builder()
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
                        .build()).
                creatorId("d41f2c7b-c04e-4c2a-b084-8bec13261637").
                status(Status.PENDING).
                businessHours(new ArrayList<>(Arrays.asList(BusinessHour.builder().
                        dayOfWeek("SUNDAY").
                        openAt("00:00:00").
                        closeAt("23:59:00").
                        build()))).
                audit(Audit.builder()
                        .createdAt(LocalDateTime.now())
                        .build())
                .build();

    }

    public static Seller create(String countryCode, SellerDTORequest request) {
        return Seller.builder()
                .code(UUID.randomUUID().toString())
                .countryCode(countryCode)
                .identification(Identification.builder()
                        .type(request.identification().type())
                        .code(request.identification().code())
                        .build())
                .name(request.name())
                .displayName(request.displayName())
                .contacts(request.contacts().stream()
                        .map(contact -> Contact.builder()
                                .type(contact.type())
                                .value(contact.value())
                                .build())
                        .collect(Collectors.toList())
                )
                .address(Address.builder()
                        .location(Location.builder()
                                .geoCoordinates(GeoCoordinates.builder()
                                        .latitude(request.address().location().geoCoordinates().latitude())
                                        .longitude(request.address().location().geoCoordinates().longitude())
                                        .build())
                                .city(request.address().location().city())
                                .country(request.address().location().country())
                                .state(request.address().location().state())
                                .number(request.address().location().number())
                                .zipCode(request.address().location().zipCode())
                                .streetAddress(request.address().location().streetAddress())
                                .build())
                        .build())
                .creatorId(request.creatorId())
                .status(Status.PENDING)
                .businessHours(request.businessHours().stream()
                        .map(businessHour -> BusinessHour.builder()
                                .dayOfWeek(businessHour.dayOfWeek())
                                .openAt(businessHour.openAt())
                                .closeAt(businessHour.closeAt())
                                .build())
                        .collect(Collectors.toList()))
                .audit(Audit.builder().createdAt(LocalDateTime.now()).build())
                .build();
    }

}