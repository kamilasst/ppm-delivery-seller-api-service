package com.ppm.delivery.seller.api.service.exception;

public class MessageErrorConstants {

    public static final String ERROR_COUNTRY_REQUIRED_HEADER = "Country is required in the request header";
    public static final String ERROR_COUNTRY_NOT_SUPPORTED = "Country not supported: %s";
    public static final String ERROR_IDENTIFICATION_CODE_ALREADY_EXISTS = "The identification code is already exists";
    public static final String ERROR_SELLER_NOT_FOUND = "Seller not found";
    public static final String ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED = "Status or business hours are required";
    public static final String ERROR_OPERATION_NOT_PERMITTED_FOR_THIS_PROFILE = "Operation is not permitted for the user's profile";
    public static final String ERROR_PROFILE_REQUIRED_HEADER = "Profile is required in the request header";
    public static final String ERROR_PROFILE_IS_INVALID = "Profile is Invalid";
}
