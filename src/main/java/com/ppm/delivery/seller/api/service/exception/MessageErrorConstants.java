package com.ppm.delivery.seller.api.service.exception;

public class MessageErrorConstants {

    public static final String ERROR_COUNTRY_REQUIRED_HEADER = "Country is required in the request header";
    public static final String ERROR_COUNTRY_NOT_SUPPORTED = "Country not supported: %s";
    public static final String ERROR_IDENTIFICATION_CODE_ALREADY_EXISTS = "The identification code is already exists";
    public static final String ERROR_SELLER_NOT_FOUND = "Seller not found";
    public static final String ERROR_STATUS_AND_BUSINESSHOUR_MUST_BE_PROVIDED = "Status and business hours must be provided";

}
