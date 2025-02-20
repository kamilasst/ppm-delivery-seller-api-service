package com.ppm.delivery.seller.api.service.api.interceptor;

import com.ppm.delivery.seller.api.service.api.config.SellerConfig;
import com.ppm.delivery.seller.api.service.api.domain.request.header.Header;
import com.ppm.delivery.seller.api.service.exception.CountryNotSupportedException;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequestValidator {

    private SellerConfig sellerConfig;

    public RequestValidator(SellerConfig sellerConfig){
        this.sellerConfig = sellerConfig;
    }

    public boolean validate(final Header header){

        final String country = header.metadata().country();
        if (StringUtils.isBlank(country)) {
            throw new CountryNotSupportedException(MessageErrorConstants.ERROR_COUNTRY_REQUIRED_HEADER);
        }

        List<String> supportedCountries = sellerConfig.getSupportedCountries();
        if (supportedCountries == null || !supportedCountries.contains(country)) {
            throw new CountryNotSupportedException(String.format(MessageErrorConstants.ERROR_COUNTRY_NOT_SUPPORTED, country));
        }
        return true;
    }

}
