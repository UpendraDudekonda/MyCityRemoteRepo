package com.mycity.location.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import jakarta.annotation.PostConstruct;

@Configuration
@EnableWebFluxSecurity
public class LocationSecurityConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(LocationSecurityConfig.class);
	
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		logger.info("Security Has Been Called");
	    return http
	        .csrf(ServerHttpSecurity.CsrfSpec::disable)
	        .authorizeExchange(exchange -> exchange
	            .pathMatchers("/location/**").permitAll()
	            .anyExchange().authenticated()
	        )
	        .build();
	}
	

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*"); // 👈 Allow all origins for testing
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true); // allow cookies, etc.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
    @PostConstruct
    public void init() {
        System.out.println("✅ LocationSecurityConfig loaded");
    }

}
