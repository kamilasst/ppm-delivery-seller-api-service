package com.ppm.delivery.seller.api.service.builder;

import com.ppm.delivery.seller.api.service.api.domain.request.AddressDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.GeoCoordinatesDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.LocationDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerAvailableNearbyDTOResponse;
import com.ppm.delivery.seller.api.service.domain.model.Identification;
import com.ppm.delivery.seller.api.service.domain.model.Seller;

import java.util.stream.Collectors;

public class SellerAvailableNearbyDTOResponseBuilder {

    public static SellerAvailableNearbyDTOResponse createDefault(Seller seller) {

        return new SellerAvailableNearbyDTOResponse(
                seller.getCode(),
                seller.getCountryCode(),
                new Identification(
                        seller.getIdentification().getType(),
                        seller.getIdentification().getCode()
                ),
                seller.getName(),
                seller.getDisplayName(),
                seller.getContacts().stream()
                        .map(c -> new SellerAvailableNearbyDTOResponse.ContactDTO(
                                c.getType(),
                                c.getValue()
                        ))
                        .collect(Collectors.toList()),
                new AddressDTORequest(
                        new LocationDTORequest(
                                new GeoCoordinatesDTORequest(
                                        seller.getAddress().getLocation().getGeoCoordinates().getLatitude(),
                                        seller.getAddress().getLocation().getGeoCoordinates().getLongitude()
                                ),
                                seller.getAddress().getLocation().getCity(),
                                seller.getAddress().getLocation().getCountry(),
                                seller.getAddress().getLocation().getState(),
                                seller.getAddress().getLocation().getNumber(),
                                seller.getAddress().getLocation().getZipCode(),
                                seller.getAddress().getLocation().getStreetAddress()
                        )
                ),
                seller.getStatus(),
                seller.getBusinessHours().stream()
                        .map(b -> new SellerAvailableNearbyDTOResponse.BusinessHourDTO(
                                b.getDayOfWeek(),
                                b.getOpenAt(),
                                b.getCloseAt()
                        ))
                        .collect(Collectors.toList())
        );
    }
}
