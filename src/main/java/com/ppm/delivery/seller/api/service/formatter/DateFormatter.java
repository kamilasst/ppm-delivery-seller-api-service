package com.ppm.delivery.seller.api.service.formatter;

import com.ppm.delivery.seller.api.service.constantsApi.ConstantsMocks;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

// TODO atg ReviewCode: Classe está sendo usada ? Se sim, criar testes unitário para o metodo
public class DateFormatter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern(ConstantsMocks.ISO_UTC_MILLISECONDS_PATTERN)
            .withZone(ZoneOffset.UTC);

    private DateFormatter(){}

    public static String format(Instant instant){
        return FORMATTER.format(instant);
    }

}