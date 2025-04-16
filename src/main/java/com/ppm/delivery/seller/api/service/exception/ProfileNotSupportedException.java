package com.ppm.delivery.seller.api.service.exception;

public class ProfileNotSupportedException extends HeaderValidationException{
    public ProfileNotSupportedException(String message) {
        super(message);
    }
}