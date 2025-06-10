package com.ppm.delivery.seller.api.service.repository;

import com.ppm.delivery.seller.api.service.domain.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ISellerRepository extends JpaRepository<Seller, String> {

    boolean existsByIdentificationCode(String identificationCode);
    Optional<Seller> findByCode(String code);

    @Query(value = """
   SELECT * FROM seller s
   WHERE s.status = 'ACTIVE'
   AND (
       6371000 * acos(
           cos(radians(:lat)) * cos(radians(s.location_latitude)) *
           cos(radians(s.location_longitude) - radians(:lng)) +
           sin(radians(:lat)) * sin(radians(s.location_latitude))
       )
   ) <= :radius
""", nativeQuery = true)
    List<Seller> findActiveSellersNear(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radius") double radiusInMeters
    );

}
