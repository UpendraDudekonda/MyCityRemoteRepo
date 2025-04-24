package com.mycity.media.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebSecurity
public class MediaSecurityConfig {
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(csrf -> csrf.disable())
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/media/upload/image","/media/images","/media/upload","/media/cover-image","/media/fetch{id}")
	            .permitAll()
	            .anyRequest().authenticated()
	        );
	    return http.build();
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
}