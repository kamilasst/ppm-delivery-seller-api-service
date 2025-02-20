package com.ppm.delivery.seller.api.service.exception;

public class CountryNotSupportedException extends RuntimeException{

    public CountryNotSupportedException(String message) {
        super(message);
    }
}
