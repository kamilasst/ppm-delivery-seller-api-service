package com.ppm.delivery.seller.api.service.utils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

// TODO atg ReviewCode: Vamos evitar criar clases e pacotes "utils" pois é muito genérico, crie pacotes mais específicos, como
// por exemplo nesse caso classe DateFormatter e pacote "formatter"

// TODO atg ReviewCode: Classe está sendo usada ? Se sim, criar testes unitário para o metodo
public class DateFormatterUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")  // TODO atg ReviewCode: Por favor crie constante para futuro reuso
            .withZone(ZoneOffset.UTC);

    private DateFormatterUtil(){}

    public static String format(Instant instant){
        return FORMATTER.format(instant);
    }

}