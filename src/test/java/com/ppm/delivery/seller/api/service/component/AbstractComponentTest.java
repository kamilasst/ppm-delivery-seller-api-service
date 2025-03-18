package com.ppm.delivery.seller.api.service.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppm.delivery.seller.api.service.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;


public abstract class AbstractComponentTest {

    @Autowired
    protected SellerRepository sellerRepository;

    @Autowired
    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

}
