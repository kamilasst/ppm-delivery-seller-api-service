package com.ppm.delivery.seller.api.service.repository;

import com.ppm.delivery.seller.api.service.domain.model.Seller;

public interface ISellerRepository {

    Seller save(String countryCode, Seller seller);
    boolean isCodeExists(String countryCode, String code);

}