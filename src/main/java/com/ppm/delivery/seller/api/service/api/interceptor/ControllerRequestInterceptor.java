package com.ppm.delivery.seller.api.service.api.interceptor;

import com.ppm.delivery.seller.api.service.api.request.header.Event;
import com.ppm.delivery.seller.api.service.api.request.header.Header;
import com.ppm.delivery.seller.api.service.api.request.header.Metadata;
import com.ppm.delivery.seller.api.service.api.request.header.UserInfo;
import com.ppm.delivery.seller.api.service.api.util.ContextConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ControllerRequestInterceptor implements HandlerInterceptor {

    private final RequestValidator requestValidator;


    public ControllerRequestInterceptor(final RequestValidator requestValidator) {
        this.requestValidator = requestValidator;
    }

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler){

        final String correlationId = request.getHeader(ContextConstants.HEADER_CORRELATION_ID);
        final String timestamp = request.getHeader(ContextConstants.HEADER_TIMESTAMP);
        final String source = request.getHeader(ContextConstants.HEADER_SOURCE);
        final String authorization = request.getHeader(ContextConstants.HEADER_AUTHORIZATION);
        final String type = request.getHeader(ContextConstants.HEADER_TYPE);
        final String eventName = request.getHeader(ContextConstants.HEADER_EVENT_NAME);
        final String eventVersion = request.getHeader(ContextConstants.HEADER_EVENT_VERSION);
        final String userInfoId = request.getHeader(ContextConstants.HEADER_USER_INFO_ID);
        final String userInfoProfile = request.getHeader(ContextConstants.HEADER_USER_INFO_PROFILE);
        final String country = request.getHeader(ContextConstants.HEADER_COUNTRY);
        final String platform = request.getHeader(ContextConstants.HEADER_PLATFORM);

        final Header header = new Header(
                correlationId,
                timestamp,
                source,
                authorization,
                type,
                new Event(eventName, eventVersion),
                new UserInfo(userInfoId, userInfoProfile),
                new Metadata(country, platform)
        );

        requestValidator.validate(header);
        return true;
    }

}
