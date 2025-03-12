package com.ppm.delivery.seller.api.service.repository;

import com.ppm.delivery.seller.api.service.domain.model.Seller;

public interface ISellerRepository {

    Seller save(Seller seller, String countryCode);
    Boolean findByCode(String code, String countryCode);

}