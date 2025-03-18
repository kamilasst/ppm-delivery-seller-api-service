package com.ppm.delivery.seller.api.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc // TODO atg Investigar para q serve
public class PpmDeliverySellerApiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PpmDeliverySellerApiServiceApplication.class, args);
	}

}
