package com.mycity.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtservice;
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtService jwtservice, JwtFilter jwtFilter) {
        this.jwtservice = jwtservice;
        this.jwtFilter = jwtFilter;

    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/**") // Apply this chain to all requests
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .requestCache(requestCache -> requestCache.disable())
            .authorizeHttpRequests(auth -> auth
                // Permit access to admin registration and login
                .requestMatchers(
                    "/auth/user/register"
                ).permitAll()
                .requestMatchers(
                    "/auth/user/login"
                ).permitAll()
                // Permit access to merchant registration and login
                .requestMatchers(
                    "/auth/merchant/register"
                ).permitAll()
                .requestMatchers(
                    "/auth/merchant/login"
                ).permitAll()
                // Optionally, you might have a general /auth/** that you want to permit partially
                // If so, ensure the more specific rules above come first
                .requestMatchers(
                    "/auth/**" // Consider if you still need this broad permission
                ).permitAll()
                .anyRequest().authenticated() // All other requests require JWT
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT Filter
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}