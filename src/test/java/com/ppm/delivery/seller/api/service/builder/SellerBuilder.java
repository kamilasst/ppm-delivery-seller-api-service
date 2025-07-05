package com.ppm.delivery.seller.api.service.builder;

import com.ppm.delivery.seller.api.service.api.domain.request.*;
import com.ppm.delivery.seller.api.service.constants.SellerMockConstants;
import com.ppm.delivery.seller.api.service.domain.model.*;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import com.ppm.delivery.seller.api.service.constants.ConstantsMocks;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SellerBuilder {

    public static Seller createDefault(String countryCode){
        Seller seller = Seller.builder()
                .code(UUID.randomUUID().toString())
                .countryCode(countryCode)
                .identification(Identification.builder()
                        .type(SellerMockConstants.DEFAULT_IDENTIFICATION_TYPE)
                        .code(SellerMockConstants.DEFAULT_IDENTIFICATION_CODE)
                        .build())
                .name(SellerMockConstants.DEFAULT_NAME)
                .displayName(SellerMockConstants.DEFAULT_DISPLAY_NAME)
                .contacts(List.of(Contact.builder()
                        .type(SellerMockConstants.DEFAULT_CONTACT_TYPE)
                        .value(SellerMockConstants.DEFAULT_CONTACT_VALUE)
                        .build()))
                .address(Address.builder()
                        .location(Location.builder()
                                .geoCoordinates(GeoCoordinates.builder()
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
                        .build()).
                creatorId(SellerMockConstants.DEFAULT_CREATOR_ID).
                status(Status.PENDING).
                businessHours(new ArrayList<>(Arrays.asList(BusinessHour.builder().
                        dayOfWeek(SellerMockConstants.DEFAULT_DAY_OF_WEEK).
                        openAt(ConstantsMocks.TIME_00h00m00).
                        closeAt(ConstantsMocks.TIME_23h59m00).
                        build()))).
                audit(Audit.builder()
                        .createdAt(LocalDateTime.now())
                        .build())
                .build();

        seller.getContacts().forEach(contact -> contact.setSeller(seller));
        seller.getBusinessHours().forEach(businessHour -> businessHour.setSeller(seller));

        return seller;
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
                                .dayOfWeek(businessHour.getDayOfWeek().toString())
                                .openAt(businessHour.getOpenAt())
                                .closeAt(businessHour.getCloseAt())
                                .build())
                        .collect(Collectors.toList()))
                .audit(Audit.builder().createdAt(LocalDateTime.now()).build())
                .build();
    }

    public static Seller createAllowingNullBusinessHours(String countryCode, SellerDTORequest request) {
        List<BusinessHour> businessHours = request.businessHours() != null
                ? request.businessHours().stream()
                .map(businessHour -> BusinessHour.builder()
                        .dayOfWeek(businessHour.getDayOfWeek().toString())
                        .openAt(businessHour.getOpenAt())
                        .closeAt(businessHour.getCloseAt())
                        .build())
                .collect(Collectors.toList())
                : Collections.emptyList();

        Seller seller = Seller.builder()
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
                        .collect(Collectors.toList()))
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
                .businessHours(businessHours)
                .audit(Audit.builder().createdAt(LocalDateTime.now()).build())
                .build();

        seller.getContacts().forEach(contact -> contact.setSeller(seller));
        seller.getBusinessHours().forEach(bh -> bh.setSeller(seller));

        return seller;
    }

    public static Seller createWithoutBusinessHours(String countryCode, SellerDTORequest request) {
        Seller seller = Seller.builder()
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
                        .collect(Collectors.toList()))
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
                .audit(Audit.builder().createdAt(LocalDateTime.now()).build())
                .build();

        seller.getContacts().forEach(contact -> contact.setSeller(seller));

        return seller;
    }

    public static Seller createWithCoordinates(String countryCode, Status status, double latitude, double longitude) {
        Seller seller = createDefault(countryCode);
        seller.setStatus(status);
        seller.setIdentification(Identification.builder()
                .type(SellerMockConstants.DEFAULT_IDENTIFICATION_TYPE)
                .code(UUID.randomUUID().toString())
                .build());

        if (seller.getAddress() == null) seller.setAddress(Address.builder().build());
        if (seller.getAddress().getLocation() == null) seller.getAddress().setLocation(Location.builder().build());
        if (seller.getAddress().getLocation().getGeoCoordinates() == null) {
            seller.getAddress().getLocation().setGeoCoordinates(GeoCoordinates.builder().build());
        }
        seller.getAddress().getLocation().getGeoCoordinates().setLatitude(latitude);
        seller.getAddress().getLocation().getGeoCoordinates().setLongitude(longitude);

        return seller;
    }

}