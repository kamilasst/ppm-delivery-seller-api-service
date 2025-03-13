package com.ppm.delivery.seller.api.service.repository;

import com.ppm.delivery.seller.api.service.domain.model.*;
import com.ppm.delivery.seller.api.service.utils.DataBaseConstants;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class SellerRepository implements ISellerRepository {

    private final EntityManager entityManager;

    public SellerRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Seller save(String countryCode, Seller seller) {

        insertSeller(countryCode, seller);
        insertContact(countryCode, seller);
        insertBusinessHour(countryCode, seller);
        return seller;

    }

    private void insertSeller(String countryCode, Seller seller) {
        String tableName = getTableName(countryCode, DataBaseConstants.TABLE_SELLER);

        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ")
                .append(tableName)
                .append(" (code, identification_type, identification_code, name, display_name, " +
                        "location_latitude, location_longitude, city, country, state, number, zip_code, street_address, " +
                        "creator_id, status, create_at, update_at) VALUES " +
                        "(:code, :idType, :idCode, :name, :displayName, :lat, :long, :city, :country, :state, :number, :zip, " +
                        ":street, :creator, :status, :createAt, :updateAt)");

        executeSellerInsert(seller, query);
    }

    private void executeSellerInsert(Seller seller, StringBuilder query) {

        Location location = seller.getAddress().getLocation();
        GeoCoordinates geoCoordinates = seller.getAddress().getLocation().getGeoCoordinates();
        Audit audit = seller.getAudit();

        entityManager.createNativeQuery(query.toString())
                .setParameter("code", seller.getCode())
                .setParameter("idType", seller.getIdentification().getType())
                .setParameter("idCode", seller.getIdentification().getCode())
                .setParameter("name", seller.getName())
                .setParameter("displayName", seller.getDisplayName())
                .setParameter("lat", geoCoordinates.getLatitude())
                .setParameter("long", geoCoordinates.getLongitude())
                .setParameter("city", location.getCity())
                .setParameter("country", location.getCountry())
                .setParameter("state", location.getState())
                .setParameter("number", location.getNumber())
                .setParameter("zip", location.getZipCode())
                .setParameter("street", location.getStreetAddress())
                .setParameter("creator", seller.getCreatorId())
                .setParameter("status", seller.getStatus().name())
                .setParameter("createAt", audit.getCreateAt())
                .setParameter("updateAt", audit.getUpdateAt())
                .executeUpdate();
    }

    private void insertContact(String countryCode, Seller seller) {
        String contactTable = getTableName(countryCode, DataBaseConstants.TABLE_CONTACT);

        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ")
                .append(contactTable)
                .append(" (type, value, seller_code) VALUES " +
                        "(:type, :value, :sellerCode)");

        executeContactInsert(seller, query);
    }

    private void executeContactInsert(Seller seller, StringBuilder query) {
        for (Contact contact : seller.getContacts()) {
            entityManager.createNativeQuery(query.toString())
                    .setParameter("type", contact.getType())
                    .setParameter("value", contact.getValue())
                    .setParameter("sellerCode", seller.getCode())
                    .executeUpdate();
        }
    }

    private void insertBusinessHour(String countryCode, Seller seller) {
        String businessHourTable = getTableName(countryCode, DataBaseConstants.TABLE_BUSINESS_HOUR);

        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ")
                .append(businessHourTable)
                .append(" (day_of_week, open_at, close_at, seller_code) VALUES " +
                        "(:dayOfWeek, :openAt, :closeAt, :sellerCode)");

        executeBusinessHourInsert(seller, query);
    }

    private void executeBusinessHourInsert(Seller seller, StringBuilder query) {
        for (BusinessHour businessHour : seller.getBusinessHours()) {
            entityManager.createNativeQuery(query.toString())
                    .setParameter("dayOfWeek", businessHour.getDayOfWeek())
                    .setParameter("openAt", businessHour.getOpenAt())
                    .setParameter("closeAt", businessHour.getCloseAt())
                    .setParameter("sellerCode", seller.getCode())
                    .executeUpdate();
        }
    }

    public boolean isCodeExists(String countryCode, String code) {
        String tableName = getTableName(countryCode, DataBaseConstants.TABLE_SELLER);

        StringBuilder query = new StringBuilder();
        query.append("SELECT 1 FROM ")
                .append(tableName)
                .append(" WHERE identification_code = :code");

        try {
            entityManager.createNativeQuery(query.toString())
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
