package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.api.domain.request.BusinessHourDTORequest;
import com.ppm.delivery.seller.api.service.domain.model.BusinessHour;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class SellerUpdateHelper {

    public void updateStatus(Status status, Seller seller) {
        if (Objects.nonNull(status)) {
            seller.setStatus(status);
        }
    }

    public void updateBusinessHours(List<BusinessHourDTORequest> businessHoursDTOs, Seller seller) {
        if (CollectionUtils.isEmpty(businessHoursDTOs)) {
            return;
        }

        List<BusinessHour> existingBusinessHours = seller.getBusinessHours();

        for (BusinessHourDTORequest dto : businessHoursDTOs) {
            Optional<BusinessHour> existing = existingBusinessHours.stream()
                    .filter(bh -> bh.getDayOfWeek().equals(dto.getDayOfWeek()))
                    .findFirst();

            if (existing.isPresent()) {
                BusinessHour businessHour = existing.get();
                businessHour.setOpenAt(dto.getOpenAt());
                businessHour.setCloseAt(dto.getCloseAt());
            } else {
                BusinessHour newBusinessHour = BusinessHour.builder()
                        .dayOfWeek(dto.getDayOfWeek())
                        .openAt(dto.getOpenAt())
                        .closeAt(dto.getCloseAt())
                        .seller(seller)
                        .build();
                existingBusinessHours.add(newBusinessHour);
            }
        }
        seller.getAudit().setUpdatedAt(LocalDateTime.now());
    }

}