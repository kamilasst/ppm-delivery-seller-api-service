package com.ppm.delivery.seller.api.service.api.controller;

import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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

    @Override
    public ResponseEntity<Map<String, Object>> patchV1(@PathVariable String code,
                                                       @RequestBody Map<String, Object> patchSeller) {
        return ResponseEntity.ok(patchSeller);
    }

}
