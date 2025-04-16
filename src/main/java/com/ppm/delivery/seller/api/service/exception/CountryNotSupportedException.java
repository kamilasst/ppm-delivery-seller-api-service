package com.ppm.delivery.seller.api.service.exception;

public class CountryNotSupportedException extends HeaderValidationException{
    public CountryNotSupportedException(String message) {
        super(message);
    }
}
