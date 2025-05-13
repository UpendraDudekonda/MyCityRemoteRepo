package com.mycity.media.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public SecurityFilterChain filterChainn(HttpSecurity http) throws Exception {
	    http
	        .csrf(csrf -> csrf.disable())
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/media/upload/images", "/media/delete/images/**", "/media/update/images/**", "/media/fetch/images/**")
	            .permitAll() 
	            .anyRequest().authenticated()
	        );
	    return http.build();
	}
	
	
	
	 @Bean(name="customeBean")
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


}
