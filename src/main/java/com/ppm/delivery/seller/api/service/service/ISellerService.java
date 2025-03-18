package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerUpdateDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerUpdateDTOResponse;

public interface ISellerService {

    SellerDTOResponse create(SellerDTORequest sellerDTORequest);
    SellerUpdateDTOResponse update(String code, SellerUpdateDTORequest sellerUpdateDTORequest);

}
