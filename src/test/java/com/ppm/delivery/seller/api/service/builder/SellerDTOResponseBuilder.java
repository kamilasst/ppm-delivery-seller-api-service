package com.ppm.delivery.seller.api.service.builder;

import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.domain.model.Seller;

public class SellerDTOResponseBuilder {

    public static SellerDTOResponse create(Seller seller) {
        return SellerDTOResponse.builder()
                .code(seller.getCode())
                .status(seller.getStatus())
                .createdDate(seller.getAudit().getCreatedAt())
                .build();


    }
}
