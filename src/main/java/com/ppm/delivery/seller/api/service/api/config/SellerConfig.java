package com.ppm.delivery.seller.api.service.api.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.seller")
@Getter
@Setter
public class SellerConfig {

    @NotEmpty(message = "Supported countries cannot be empty.")
    private List<String> supportedCountries  = new ArrayList<>();

}
