package com.ppm.delivery.seller.api.service.repository;

import com.ppm.delivery.seller.api.service.domain.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<Seller, String> {

    // TODO atg Review Renomear for identificationCode
    boolean existsByIdentificationCode(String IdentificationCode);

}
