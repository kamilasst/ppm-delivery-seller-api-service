package com.ppm.delivery.seller.api.service.api.controller;

import com.ppm.delivery.seller.api.service.api.domain.request.SellerDTORequest;
import com.ppm.delivery.seller.api.service.api.domain.response.SellerDTOResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface ISellerController {

    @PostMapping("/create")
    ResponseEntity<SellerDTOResponse> create(@Valid @RequestBody SellerDTORequest code);

    @PatchMapping("/{code}")
    ResponseEntity<Map<String, Object>> patchV1(@Valid @PathVariable String code, @RequestBody Map<String, Object> patchSeller);

}
