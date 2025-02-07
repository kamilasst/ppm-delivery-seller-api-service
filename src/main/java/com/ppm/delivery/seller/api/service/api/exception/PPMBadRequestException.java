package com.ppm.delivery.seller.api.service.api.exception;

import lombok.Getter;

@Getter
public class PPMBadRequestException extends RuntimeException{

    private final String message;

    public PPMBadRequestException(String message) {
        super(message);
        this.message = message;
    }

}
