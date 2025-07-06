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

    // TODO Review GET - Por favor avalie renomear para searchAvailableNearby
    // TODO Review GET - Como o country é uma informacao 'forte', avalie ser a primeira cláusula do WHERE
    @Query(value = """
               SELECT s.* FROM seller s
               JOIN business_hour bh ON s.code = bh.seller_code
               WHERE s.status = 'ACTIVE'
               AND s.country_code = :countryCode
               AND (
                   6371000 * acos(
                       cos(radians(:lat)) * cos(radians(s.location_latitude)) *
                       cos(radians(s.location_longitude) - radians(:lng)) +
                       sin(radians(:lat)) * sin(radians(s.location_latitude))
                   )
               ) <= :radius
            AND bh.day_of_week = :dayOfWeek
            AND CAST(:orderHours AS TIME)
            BETWEEN CAST(bh.open_at AS TIME) AND CAST(bh.close_at AS TIME)
            """, nativeQuery = true)
    List<Seller> findActiveSellersNear(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radius") double radiusInMeters,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("orderHours") String orderHours,
            @Param("countryCode") String countryCode
    );

}
