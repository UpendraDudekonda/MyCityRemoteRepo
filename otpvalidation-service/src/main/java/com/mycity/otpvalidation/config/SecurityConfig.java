package com.mycity.otpvalidation.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
           .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/otp/auth/verifyotp"
                    
                ).permitAll()
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

	        config.setAllowCredentials(true);
	 
	        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

	        source.registerCorsConfiguration("/**", config);

	        return new CorsWebFilter(source);

	    }
	
}
