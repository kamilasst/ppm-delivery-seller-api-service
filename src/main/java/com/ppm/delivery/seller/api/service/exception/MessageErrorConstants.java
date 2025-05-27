package com.ppm.delivery.seller.api.service.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageErrorConstants {

    public static final String ERROR_COUNTRY_REQUIRED_HEADER = "Country is required in the request header";
    public static final String ERROR_COUNTRY_NOT_SUPPORTED = "Country not supported: %s";
    public static final String ERROR_IDENTIFICATION_CODE_ALREADY_EXISTS = "The identification code is already exists";
    public static final String ERROR_SELLER_NOT_FOUND = "Seller not found";
    public static final String ERROR_STATUS_OR_BUSINESS_HOURS_ARE_REQUIRED = "Status or business hours are required";
    public static final String ERROR_OPERATION_NOT_PERMITTED_FOR_THIS_PROFILE = "Operation is not permitted for the user's profile";
    public static final String ERROR_PROFILE_REQUIRED_HEADER = "Profile is required in the request header";
    public static final String ERROR_PROFILE_IS_INVALID = "Profile is Invalid";
    public static final String ERROR_REQUEST_BODY_IS_REQUIRED = "Request body is required";
    public static final String ERROR_AT_LEAST_ONE_BUSINESS_HOUR_REQUIRED = "At least one business hour must be provided.";
    public static final String ERROR_OPENING_TIME_MUST_BE_BEFORE_CLOSING_TIME = "Opening time must be before closing time";
    public static final String ERROR_DAY_OF_WEEK_MUST_BE_PROVIDED = "Day of week must be provided.";
    public static final String ERROR_OPEN_TIME_MUST_BE_PROVIDED = "Open time must be provided.";
    public static final String ERROR_CLOSE_TIME_MUST_BE_PROVIDED = "Close time must be provided.";

}
