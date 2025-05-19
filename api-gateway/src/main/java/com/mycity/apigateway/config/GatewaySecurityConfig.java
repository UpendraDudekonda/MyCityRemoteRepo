package com.mycity.apigateway.config;
 
import org.springframework.cloud.client.loadbalancer.LoadBalanced;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

import org.springframework.security.config.web.server.ServerHttpSecurity;

import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.reactive.CorsWebFilter;

import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import org.springframework.web.reactive.function.client.WebClient;
 
 
@Configuration

@EnableWebFluxSecurity

public class GatewaySecurityConfig {
 
	@Bean

	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

	    return http

	        .csrf(ServerHttpSecurity.CsrfSpec::disable)

	        .authorizeExchange(exchange -> exchange

	            .anyExchange().permitAll() 

	        )

	        .build();

	}
 
 
    @Bean
    
    public CorsWebFilter corsWebFilter() {

        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("*"); // TODO: For production, replace * with trusted domains

        config.addAllowedHeader("*");

        config.addAllowedMethod("*");

        config.setAllowCredentials(true);
 
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);

    }
 
    @Bean
    
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {

        return WebClient.builder();

    }

}

 