package com.ppm.delivery.seller.api.service.api.request.header;

public record Header(
        String correlationId,
        String timestamp,
        String source,
        String authorization,
        String type,
        Event event,
        UserInfo userInfo,
        Metadata metadata

) {
}
