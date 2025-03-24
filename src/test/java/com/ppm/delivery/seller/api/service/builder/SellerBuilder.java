package com.ppm.delivery.seller.api.service.builder;

import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.domain.model.*;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

public class SellerBuilder {

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