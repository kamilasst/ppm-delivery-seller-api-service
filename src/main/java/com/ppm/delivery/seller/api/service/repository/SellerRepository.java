package com.ppm.delivery.seller.api.service.repository;

import com.ppm.delivery.seller.api.service.domain.model.BusinessHour;
import com.ppm.delivery.seller.api.service.domain.model.Contact;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
@RequiredArgsConstructor
public class SellerRepository implements ISellerRepository {

    private final EntityManager entityManager;

    public Seller save(String countryCode, Seller seller){

        String tableName = getTableName(countryCode, "_seller");
        entityManager.createNativeQuery(
                        "INSERT INTO " + tableName + " (code, identification_type, identification_code, name, display_name, " +
                                "location_latitude, location_longitude, city, country, state, number, zip_code, street_address, " +
                                "creator_id, status, create_at, update_at) VALUES " +
                                "(:code, :idType, :idCode, :name, :displayName, :lat, :long, :city, :country, :state, :number, :zip, " +
                                ":street, :creator, :status, :createAt, :updateAt)")
                .setParameter("code", seller.getCode())
                .setParameter("idType", seller.getIdentification().getType())
                .setParameter("idCode", seller.getIdentification().getCode())
                .setParameter("name", seller.getName())
                .setParameter("displayName", seller.getDisplayName())
                .setParameter("lat", seller.getAddress().getLocation().getGeoCoordinates().getLatitude())
                .setParameter("long", seller.getAddress().getLocation().getGeoCoordinates().getLongitude())
                .setParameter("city", seller.getAddress().getLocation().getCity())
                .setParameter("country", seller.getAddress().getLocation().getCountry())
                .setParameter("state", seller.getAddress().getLocation().getState())
                .setParameter("number", seller.getAddress().getLocation().getNumber())
                .setParameter("zip", seller.getAddress().getLocation().getZipCode())
                .setParameter("street", seller.getAddress().getLocation().getStreetAddress())
                .setParameter("creator", seller.getCreatorId())
                .setParameter("status", seller.getStatus().name())
                .setParameter("createAt", seller.getAudit().getCreateAt())
                .setParameter("updateAt", seller.getAudit().getUpdateAt())
                .executeUpdate();

        String contactTable = getTableName(countryCode, "_contact");
        for (Contact contact : seller.getContacts()) {
            entityManager.createNativeQuery(
                            "INSERT INTO " + contactTable + " (type, value, seller_code) VALUES " +
                                    "(:type, :value, :sellerCode)")
                    .setParameter("type", contact.getType())
                    .setParameter("value", contact.getValue())
                    .setParameter("sellerCode", seller.getCode())
                    .executeUpdate();
        }

        String businessHourTable = getTableName(countryCode, "_business_hour");
        for (BusinessHour businessHour : seller.getBusinessHours()) {
            entityManager.createNativeQuery(
                            "INSERT INTO " + businessHourTable + " (day_of_week, open_at, close_at, seller_code) VALUES " +
                                    "(:dayOfWeek, :openAt, :closeAt, :sellerCode)")
                    .setParameter("dayOfWeek", businessHour.getDayOfWeek())
                    .setParameter("openAt", businessHour.getOpenAt())
                    .setParameter("closeAt", businessHour.getCloseAt())
                    .setParameter("sellerCode", seller.getCode())
                    .executeUpdate();
        }

        return seller;
    }

    public Boolean findByCode(String countryCode, String code) {
        String tableName = getTableName(countryCode, "_seller");
        try {
            Object result = entityManager.createNativeQuery(
                            "SELECT 1 FROM " + tableName + " WHERE code = :code")
                    .setParameter("code", code)
                    .getSingleResult();

            return true;
        } catch (NoResultException e) {
            return false;
        }

    }

    private String getTableName(String countryCode, String tableSuffix) {
        return countryCode.toLowerCase() + tableSuffix;
    }

}
