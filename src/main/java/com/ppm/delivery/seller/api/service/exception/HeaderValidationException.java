package com.ppm.delivery.seller.api.service.exception;

public class HeaderValidationException extends RuntimeException{
    public HeaderValidationException(String message) {
        super(message);
    }
}