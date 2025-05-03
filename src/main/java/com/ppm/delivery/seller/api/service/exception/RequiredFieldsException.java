package com.ppm.delivery.seller.api.service.exception;

import java.util.List;

public class RequiredFieldsException extends RuntimeException{

    private List<String> messages;
    public RequiredFieldsException(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }

}