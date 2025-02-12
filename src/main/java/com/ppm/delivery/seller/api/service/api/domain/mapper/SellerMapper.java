package com.ppm.delivery.seller.api.service.api.domain.mapper;

import com.ppm.delivery.seller.api.service.api.model.Seller;
import com.ppm.delivery.seller.api.service.api.request.SellerDTORequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SellerMapper {

    SellerMapper INSTANCE = Mappers.getMapper(SellerMapper.class);

    Seller toEntity(SellerDTORequest sellerDTORequest);

}
