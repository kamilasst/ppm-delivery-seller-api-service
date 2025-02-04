package com.ppm.delivery.seller.api.service;

import org.springframework.boot.SpringApplication;

public class TestPpmDeliverySellerApiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(PpmDeliverySellerApiServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
