package com.ppm.delivery.seller.api.service.api.interceptor;

import com.ppm.delivery.seller.api.service.api.config.SellerConfig;
import com.ppm.delivery.seller.api.service.api.domain.request.header.Header;
import com.ppm.delivery.seller.api.service.domain.profile.Profile;
import com.ppm.delivery.seller.api.service.exception.CountryNotSupportedException;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import com.ppm.delivery.seller.api.service.exception.ProfileNotSupportedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RequestValidator {

    private SellerConfig sellerConfig;

    public RequestValidator(SellerConfig sellerConfig){
        this.sellerConfig = sellerConfig;
    }

    public void validateHeader(final Header header){
        validateCountry(header);
        validateProfile(header);
    }

    private void validateCountry(final Header header){

        final String country = header.metadata().country();

        if (StringUtils.isBlank(country)) {
            throw new CountryNotSupportedException(MessageErrorConstants.ERROR_COUNTRY_REQUIRED_HEADER);
        }

        List<String> supportedCountries = sellerConfig.getSupportedCountries();
        if (supportedCountries == null || !supportedCountries.contains(country)) {
            throw new CountryNotSupportedException(String.format(MessageErrorConstants.ERROR_COUNTRY_NOT_SUPPORTED, country));
        }
    }

    private void validateProfile(final Header header){

        final String profile = header.metadata().profile();

        if (StringUtils.isBlank(profile)) {
            throw new ProfileNotSupportedException(MessageErrorConstants.ERROR_PROFILE_REQUIRED_HEADER);
        }

        boolean isValid = Arrays.stream(Profile.values())
                .anyMatch(p -> p.name().equalsIgnoreCase(profile));

        if (!isValid) {
            throw new ProfileNotSupportedException(
                    String.format(MessageErrorConstants.ERROR_PROFILE_IS_INVALID, profile)
            );
        }
    }

}
