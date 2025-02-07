package com.ppm.delivery.seller.api.service.api.controller;

import com.ppm.delivery.seller.api.service.api.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.response.SellerDTOResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ISellerController {

    @PostMapping("/create")
    ResponseEntity<SellerDTOResponse> create(@Valid @RequestBody SellerDTORequest code);

}
