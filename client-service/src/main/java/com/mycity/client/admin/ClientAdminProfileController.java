package com.mycity.client.admin;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.client.config.CookieTokenExtractor;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientAdminProfileController {

    private final WebClient.Builder webClientBuilder;
    private final CookieTokenExtractor cookieTokenExtractor;

    private static final Logger logger = LoggerFactory.getLogger(ClientAdminProfileController.class);

    private static final String API_GATEWAY_SERVICE_NAME = "API-GATEWAY";
    private static final String ADMIN_PROFILE_PATH_ON_GATEWAY = "/admin/profile"; // Path for admin profile on gateway

    private static final String ADMIN_PROFILE_PICTURE = "/admin/profile/upload-picture";
    private static final String ADMIN_GET_PROFILE_PICTURE = "/admin/profile-picture";
    private static final String ADMIN_UPDATE_PROFILE = "/admin/profile/update";
    
    
    @GetMapping("/profile/admin")
    public Mono<ResponseEntity<String>> getAdminProfile(
            @RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie) {

        // Extract token using the injected CookieTokenExtractor
        String token = cookieTokenExtractor.extractTokenFromCookie(cookie);

        if (token == null || token.isEmpty()) {
            logger.error("❌ Missing Authorization token from cookie for admin profile");
            return Mono.just(ResponseEntity.status(400).body("Authorization token is missing"));
        }

        logger.info("📩 Forwarding Authorization token to API Gateway for admin profile: {}", token);
        System.out.println(token);

        return webClientBuilder.build()
                .get()
                .uri("lb://" + API_GATEWAY_SERVICE_NAME + ADMIN_PROFILE_PATH_ON_GATEWAY) // URI for admin profile
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)  // Forward token to API Gateway
                .retrieve()
                .toEntity(String.class)
                .map(response -> {
                    logger.info("✅ Received response from API Gateway for admin profile with status: {}", response.getStatusCode());
                    return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
                })
                .onErrorResume(ex -> {
                    logger.error("❌ Error forwarding to API Gateway for admin profile: {}", ex.getMessage());
                    return Mono.just(ResponseEntity.status(500).body("Something went wrong...."));
                });
    }
    
    
    @PatchMapping("/profile/update/admin") // changed from @PostMapping
    public Mono<ResponseEntity<String>> updateAdminProfile(
            @RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie,
            @RequestBody Map<String, Object> requestBody) { // Accepting partial update fields

        // 1. Extract JWT token from cookie
        String token = cookieTokenExtractor.extractTokenFromCookie(cookie);

        if (token == null || token.isEmpty()) {
            logger.error("❌ Missing Authorization token from cookie for admin profile");
            return Mono.just(ResponseEntity.status(400).body("Authorization token is missing"));
        }

        logger.info("📩 Forwarding token to API Gateway for admin update");
        System.out.println("JWT Token: " + token);
        System.out.println("Request Body: " + requestBody);

        // 2. Forward request with token and body to API Gateway
        return webClientBuilder.build()
                .patch() // changed from post() to patch()
                .uri("lb://" + API_GATEWAY_SERVICE_NAME + ADMIN_UPDATE_PROFILE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody) // forward the partial fields
                .retrieve()
                .toEntity(String.class)
                .map(response -> {
                    logger.info("✅ Response from API Gateway: {}", response.getStatusCode());
                    return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
                })
                .onErrorResume(ex -> {
                    logger.error("❌ Error during update: {}", ex.getMessage());
                    return Mono.just(ResponseEntity.status(500).body("Something went wrong..."));
                });
    }

    
    @PostMapping("/admin/upload-picture")
    public Mono<ResponseEntity<String>> uploadProfilePicture(
    		 @RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie,
            @RequestParam("image") MultipartFile imageFile) {
    	
    	// Extract token using the injected CookieTokenExtractor
        String token = cookieTokenExtractor.extractTokenFromCookie(cookie);
        
        logger.info("admin trying to add the profile picture from client......");

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("image", imageFile.getResource())
               .filename(imageFile.getOriginalFilename())
               .contentType(MediaType.valueOf(imageFile.getContentType()));

        return webClientBuilder.build()
                .post()
                .uri("lb://" + API_GATEWAY_SERVICE_NAME + ADMIN_PROFILE_PICTURE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .toEntity(String.class);
    }
    
    @GetMapping("/admin/profile-picture")
    public Mono<ResponseEntity<String>> getProfilePicture(@RequestHeader(HttpHeaders.COOKIE) String cookie) {
    	
    	// Extract token using the injected CookieTokenExtractor
        String token = cookieTokenExtractor.extractTokenFromCookie(cookie);
        
        logger.info("admin trying to fetch the profile picture from client......");
        
        return webClientBuilder.build()
                .get()
                .uri("lb://"+API_GATEWAY_SERVICE_NAME + ADMIN_GET_PROFILE_PICTURE)  // no userId in path
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .toEntity(String.class);
    }

    
    
    
}