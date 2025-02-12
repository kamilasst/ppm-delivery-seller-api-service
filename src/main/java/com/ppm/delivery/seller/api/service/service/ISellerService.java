package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;

public interface ISellerService {

    public SellerDTOResponse create(SellerDTORequest sellerDTORequest);
}
