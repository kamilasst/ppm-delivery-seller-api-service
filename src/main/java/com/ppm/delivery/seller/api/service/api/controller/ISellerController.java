package com.ppm.delivery.seller.api.service.api.controller;

import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.request.SellerUpdateDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerUpdateDTOResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ISellerController {

    @PostMapping("/create")
    ResponseEntity<SellerDTOResponse> create(@Valid @RequestBody SellerDTORequest code);

    @PatchMapping("/patch/{code}")
    ResponseEntity<SellerUpdateDTOResponse> patchV1(@Valid @PathVariable String code, @RequestBody SellerUpdateDTORequest sellerUpdateDTO);

}
