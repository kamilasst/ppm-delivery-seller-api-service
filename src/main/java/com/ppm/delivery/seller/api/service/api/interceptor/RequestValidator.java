package com.ppm.delivery.seller.api.service.api.interceptor;

import com.ppm.delivery.seller.api.service.api.exception.MessageErrorConstants;
import com.ppm.delivery.seller.api.service.api.exception.PPMBadRequestException;
import com.ppm.delivery.seller.api.service.api.request.header.Header;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {

    public boolean validate(final Header header){

        final String country = header.metadata().country();
        if (StringUtils.isBlank(country)) {
            throw new PPMBadRequestException(MessageErrorConstants.ERROR_COUNTRY_REQUIRED_HEADER);
        }
        return true;
    }

}
