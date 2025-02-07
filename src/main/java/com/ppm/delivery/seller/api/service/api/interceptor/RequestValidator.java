package com.ppm.delivery.seller.api.service.api.interceptor;

import com.ppm.delivery.seller.api.service.api.exception.PPMBadRequestException;
import com.ppm.delivery.seller.api.service.api.request.header.Header;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {

    public boolean validate(final Header header){

        if (StringUtils.isBlank(header.correlationId())) {
            throw new PPMBadRequestException("Correlation Id is required");
        }

        if (StringUtils.isBlank(header.timestamp())) {
            throw new PPMBadRequestException("Timestamp is required");
        }

        if (StringUtils.isBlank(header.source())) {
            throw new PPMBadRequestException("Source is required");
        }

        if (StringUtils.isBlank(header.type())) {
            throw new PPMBadRequestException("Type is required");
        }

        final String eventName = header.event().name();
        if (StringUtils.isBlank(eventName)) {
            throw new PPMBadRequestException("Event Name is required");
        }

        final String eventVersion = header.event().version();
        if (StringUtils.isBlank(eventVersion)) {
            throw new PPMBadRequestException("Event Version is required");
        }

        final String userInfoId = header.userInfo().id();
        if (StringUtils.isBlank(userInfoId)) {
            throw new PPMBadRequestException("User Info Id is required");
        }

        final String userInfoProfile = header.userInfo().profile();
        if (StringUtils.isBlank(userInfoProfile)) {
            throw new PPMBadRequestException("User Info Id is required");
        }

        final String country = header.metadata().country();
        if (StringUtils.isBlank(country)) {
            throw new PPMBadRequestException("Country is required");
        }

        final String platform = header.metadata().platform();
        if (StringUtils.isBlank(platform)) {
            throw new PPMBadRequestException("Platform is required");
        }
        return true;
    }

}
