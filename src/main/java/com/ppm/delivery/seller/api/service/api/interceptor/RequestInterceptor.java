package com.ppm.delivery.seller.api.service.api.interceptor;

import com.ppm.delivery.seller.api.service.api.domain.request.header.Event;
import com.ppm.delivery.seller.api.service.api.domain.request.header.Header;
import com.ppm.delivery.seller.api.service.api.domain.request.header.Metadata;
import com.ppm.delivery.seller.api.service.api.domain.request.header.UserInfo;
import com.ppm.delivery.seller.api.service.api.constants.HeaderConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    private final ContextHolder context;
    private final RequestValidator requestValidator;

    public RequestInterceptor(final ContextHolder context,
                              final RequestValidator requestValidator) {
        this.context = context;
        this.requestValidator = requestValidator;
    }

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler){

        final String correlationId = request.getHeader(HeaderConstants.HEADER_CORRELATION_ID);
        final String timestamp = request.getHeader(HeaderConstants.HEADER_TIMESTAMP);
        final String source = request.getHeader(HeaderConstants.HEADER_SOURCE);
        final String authorization = request.getHeader(HeaderConstants.HEADER_AUTHORIZATION);
        final String type = request.getHeader(HeaderConstants.HEADER_TYPE);
        final String eventName = request.getHeader(HeaderConstants.HEADER_EVENT_NAME);
        final String eventVersion = request.getHeader(HeaderConstants.HEADER_EVENT_VERSION);
        final String userInfoId = request.getHeader(HeaderConstants.HEADER_USER_INFO_ID);
        final String userInfoProfile = request.getHeader(HeaderConstants.HEADER_USER_INFO_PROFILE);
        final String country = request.getHeader(HeaderConstants.HEADER_COUNTRY);
        final String platform = request.getHeader(HeaderConstants.HEADER_PLATFORM);
        final String profile = request.getHeader(HeaderConstants.HEADER_PROFILE);

        context.initializeContext(country, profile);

        final Header header = new Header(
                correlationId,
                timestamp,
                source,
                authorization,
                type,
                new Event(eventName, eventVersion),
                new UserInfo(userInfoId, userInfoProfile),
                new Metadata(country, platform, profile)
        );

        requestValidator.validate(header);
        return true;
    }

}
