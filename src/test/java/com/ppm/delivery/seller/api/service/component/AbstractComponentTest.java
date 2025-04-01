package com.ppm.delivery.seller.api.service.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ppm.delivery.seller.api.service.repository.SellerRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;


public abstract class AbstractComponentTest {

    @Autowired
    protected SellerRepository sellerRepository;

    @Autowired
    protected MockMvc mockMvc;

    protected static ObjectMapper objectMapper;

    @BeforeAll
    public static void setUpOnce() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    @BeforeEach
    public void setUp() {
        sellerRepository.deleteAll();
    }

}
