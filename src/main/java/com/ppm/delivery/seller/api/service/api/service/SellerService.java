package com.ppm.delivery.seller.api.service.api.service;

import com.ppm.delivery.seller.api.service.api.domain.mapper.SellerMapper;
import com.ppm.delivery.seller.api.service.api.model.*;
import com.ppm.delivery.seller.api.service.api.model.enums.Status;
import com.ppm.delivery.seller.api.service.api.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.response.SellerDTOResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SellerService implements ISellerService {

    @Override
    public SellerDTOResponse create(SellerDTORequest sellerDTORequest) {

        Seller seller = SellerMapper.INSTANCE.toEntity(sellerDTORequest);
        seller.setCode(UUID.randomUUID().toString());
        seller.setStatus(Status.PENDING);
        seller.setAudit(Audit.builder().
                createAt(LocalDateTime.now().toString()).
                build());

        return new SellerDTOResponse(seller.getCode(), seller.getStatus(), seller.getAudit().getCreateAt());
    }

}
