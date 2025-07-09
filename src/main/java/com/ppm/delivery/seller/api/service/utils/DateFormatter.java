package com.ppm.delivery.seller.api.service.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatter {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private DateFormatter() {}

    public static String getHours(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("dateTime cannot be null");
        }
        return dateTime.format(TIME_FORMATTER);
    }
}
