package com.ppm.delivery.seller.api.service.repository;

import com.ppm.delivery.seller.api.service.domain.model.Seller;

public interface ISellerRepository {

    Seller save(String countryCode, Seller seller);
    Boolean findByCode(String countryCode, String code);

}