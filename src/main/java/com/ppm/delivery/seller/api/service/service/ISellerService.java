package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerNearSearchRequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerUpdateDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerUpdateDTOResponse;
import com.ppm.delivery.seller.api.service.domain.model.Seller;

import java.util.List;

public interface ISellerService {

    SellerDTOResponse create(SellerDTORequest sellerDTORequest);
    SellerUpdateDTOResponse update(String code, SellerUpdateDTORequest sellerUpdateDTORequest);
    List<Seller> searchAvailableNearby(SellerNearSearchRequest request);

}
