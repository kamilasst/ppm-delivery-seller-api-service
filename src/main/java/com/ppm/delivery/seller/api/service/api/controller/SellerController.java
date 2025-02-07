package com.ppm.delivery.seller.api.service.api.controller;

import com.ppm.delivery.seller.api.service.api.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.response.SellerDTOResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller")
public class SellerController implements ISellerController{

    //TODO criar o Header e saber para que ele server
    @Override
    public ResponseEntity<SellerDTOResponse> create(@Valid @RequestBody SellerDTORequest sellerDTORequest){
        System.out.println(sellerDTORequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

}
