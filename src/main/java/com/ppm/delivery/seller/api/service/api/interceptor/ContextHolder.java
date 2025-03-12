package com.ppm.delivery.seller.api.service.api.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
public class ContextHolder {

    private String country;

    public void initializeContext(final String country) {
        setCountry(country);
    }

    public String getCountry() {
        return this.country;
    }

    private void setCountry(final String country) {
        this.country = country;
    }
}
