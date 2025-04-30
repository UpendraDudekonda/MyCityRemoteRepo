package com.mycity.email.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientConfig {
	
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
    


}
