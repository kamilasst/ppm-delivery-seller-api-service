package com.ppm.delivery.seller.api.service.api.service;

import com.ppm.delivery.seller.api.service.api.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.response.SellerDTOResponse;

public interface ISellerService {

    public SellerDTOResponse create(SellerDTORequest sellerDTORequest);
}
