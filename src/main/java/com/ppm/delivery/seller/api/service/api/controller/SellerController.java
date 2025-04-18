package com.ppm.delivery.seller.api.service.api.controller;

import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerUpdateDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerUpdateDTOResponse;
import com.ppm.delivery.seller.api.service.service.ISellerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seller")
public class SellerController implements ISellerController{

    private final ISellerService sellerService;

    public SellerController(ISellerService sellerService){
        this.sellerService = sellerService;
    }

    @Override
    public ResponseEntity<SellerDTOResponse> create(@Valid @RequestBody SellerDTORequest sellerDTORequest){
        SellerDTOResponse sellerDTOResponse = sellerService.create(sellerDTORequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(sellerDTOResponse);
    }

    @Override
    public ResponseEntity<SellerUpdateDTOResponse> patchV1(@Valid @PathVariable String code, @RequestBody SellerUpdateDTORequest sellerUpdateDTO) {
        SellerUpdateDTOResponse response = sellerService.update(code, sellerUpdateDTO);
        return ResponseEntity.ok(response);
    }

}
