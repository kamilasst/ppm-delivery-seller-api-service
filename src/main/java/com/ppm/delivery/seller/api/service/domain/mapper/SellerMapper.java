package com.ppm.delivery.seller.api.service.domain.mapper;

import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerAvailableNearbyDTOResponse;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SellerMapper {

    SellerMapper INSTANCE = Mappers.getMapper(SellerMapper.class);

    Seller toEntity(SellerDTORequest sellerDTORequest);

    SellerAvailableNearbyDTOResponse toSellerAvailableNearbyDTO(Seller seller);

    List<SellerAvailableNearbyDTOResponse> toSellerAvailableNearbyDTOList(List<Seller> sellers);

}
