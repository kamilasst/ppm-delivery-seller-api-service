package com.ppm.delivery.seller.api.service.builder;

import com.ppm.delivery.seller.api.service.domain.model.BusinessHour;
import com.ppm.delivery.seller.api.service.domain.model.Seller;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BusinessHourTestBuilder {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String defaultOpenTime;
    private final String defaultCloseTime;
    private final Map<DayOfWeek, String[]> customSchedule = new EnumMap<>(DayOfWeek.class);
    private Seller seller;

    private BusinessHourTestBuilder(String openTime, String closeTime) {
        this.defaultOpenTime = openTime;
        this.defaultCloseTime = closeTime;
    }

    public static BusinessHourTestBuilder defaultSchedule() {
        return new BusinessHourTestBuilder("08:00:00", "18:00:00");
    }

    public static BusinessHourTestBuilder defaultSchedule(LocalTime openAt, LocalTime closeAt) {
        return new BusinessHourTestBuilder(
                openAt.format(TIME_FORMATTER),
                closeAt.format(TIME_FORMATTER)
        );
    }

    public BusinessHourTestBuilder withCustomTime(DayOfWeek day, String openAt, String closeAt) {
        this.customSchedule.put(day, new String[]{openAt, closeAt});
        return this;
    }

    public BusinessHourTestBuilder withCustomTime(DayOfWeek day, LocalTime openAt, LocalTime closeAt) {
        return withCustomTime(day, openAt.format(TIME_FORMATTER), closeAt.format(TIME_FORMATTER));
    }

    public BusinessHourTestBuilder forSeller(Seller seller) {
        this.seller = seller;
        return this;
    }

    public List<BusinessHour> build() {
        return Stream.of(DayOfWeek.values())
                .map(day -> {
                    String openAt = customSchedule.getOrDefault(day, new String[]{defaultOpenTime, defaultCloseTime})[0];
                    String closeAt = customSchedule.getOrDefault(day, new String[]{defaultOpenTime, defaultCloseTime})[1];

                    return BusinessHour.builder()
                            .dayOfWeek(day.name())
                            .openAt(openAt)
                            .closeAt(closeAt)
                            .seller(seller)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
