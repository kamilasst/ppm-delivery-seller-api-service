package com.ppm.delivery.seller.api.service.api.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
public class ContextHolder {

    private String country;
    private String profile;

    public void initializeContext(final String country, final String profile) {
        this.country = country;
        this.profile = profile;
    }

    public String getCountry() {
        return this.country;
    }

    public String getProfile() {
        return this.profile;
    }

}
