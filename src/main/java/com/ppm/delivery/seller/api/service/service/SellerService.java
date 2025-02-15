package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.domain.mapper.SellerMapper;
import com.ppm.delivery.seller.api.service.domain.model.Audit;
import com.ppm.delivery.seller.api.service.domain.model.Seller;
import com.ppm.delivery.seller.api.service.domain.model.enums.Status;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.repository.SellerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SellerService implements ISellerService {

    private SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository){
        this.sellerRepository = sellerRepository;
    }
    @Override
    public SellerDTOResponse create(SellerDTORequest sellerDTORequest) {

        Seller seller = SellerMapper.INSTANCE.toEntity(sellerDTORequest);
        seller.setCode(UUID.randomUUID().toString());
        seller.setStatus(Status.PENDING);
        seller.setAudit(Audit.builder().
                createAt(LocalDateTime.now().toString()).
                build());
        sellerRepository.save(seller);
        return new SellerDTOResponse(seller.getCode(), seller.getStatus(), seller.getAudit().getCreateAt());
    }

}
