package com.mycity.trip.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;



@Configuration
@EnableWebSecurity
public class TripPlannerSecurityConfig {

//	  @Value("${external.trip-planner.base-url}") // Inject the base URL
//	    private String externalApiBaseUrl;


	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
	    return http
	        .csrf(ServerHttpSecurity.CsrfSpec::disable)
	        .authorizeExchange(exchange -> exchange
	        	.pathMatchers("/tripplanner/public/**","/tripplanner/trip-plan","/api/**").permitAll()
	            .anyExchange().authenticated()
	        )
	        .build();
	}


    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*"); // TODO: For production, replace * with trusted domains
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
    
    
//    @Bean
//    // Spring will inject the 'webClientBuilder' bean defined above
//    public WebClient externalTripPlannerWebClient(WebClient.Builder webClientBuilder) {
//        return webClientBuilder
//                .baseUrl(externalApiBaseUrl) // Use the injected base URL
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
//                // You can add more specific configurations here if needed for this WebClient
//                .build(); // Build the actual WebClient instance
//    }
    
}