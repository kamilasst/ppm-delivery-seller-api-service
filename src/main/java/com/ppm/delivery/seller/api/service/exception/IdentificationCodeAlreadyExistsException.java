package com.ppm.delivery.seller.api.service.exception;

public class IdentificationCodeAlreadyExistsException extends RuntimeException{

    public IdentificationCodeAlreadyExistsException(String message) {
        super(message);
    }
}
