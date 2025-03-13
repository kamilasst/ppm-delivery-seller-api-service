package com.ppm.delivery.seller.api.service.api.controller;

import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller")
public class SellerController implements ISellerController{

    private final SellerService sellerService;

    public SellerController(SellerService sellerService){
        this.sellerService = sellerService;
    }

    @Override
    public ResponseEntity<SellerDTOResponse> create(@Valid @RequestBody SellerDTORequest sellerDTORequest){
        SellerDTOResponse sellerDTOResponse = sellerService.create(sellerDTORequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(sellerDTOResponse);
    }

}
