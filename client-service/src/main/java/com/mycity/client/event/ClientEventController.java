package com.mycity.client.event;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycity.client.config.CookieTokenExtractor;
import com.mycity.shared.eventsdto.EventsDTO;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientEventController {

    private final CookieTokenExtractor cookieTokenExtractor;
    private final WebClient.Builder webClientBuilder;

    private static final String API_GATEWAY_SERVICE_NAME = "API-GATEWAY";
    private static final String ADMIN_EVENT_PATH = "/admin/events/add";

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public Mono<ResponseEntity<String>> addEvent(
            @RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie,
            @RequestPart("event") EventsDTO eventDTO,
            @RequestPart("mainImage") MultipartFile mainImage,
            @RequestPart("galleryImages") MultipartFile[] galleryImages) {

        String token = cookieTokenExtractor.extractTokenFromCookie(cookie);
        if (token == null || token.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("Authorization token is missing"));
        }

        try {
            MultiValueMap<String, Object> multipartData = new LinkedMultiValueMap<>();

            // Convert DTO to JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            String eventJson = objectMapper.writeValueAsString(eventDTO);

            HttpHeaders jsonHeaders = new HttpHeaders();
            jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> eventEntity = new HttpEntity<>(eventJson, jsonHeaders);
            multipartData.add("event", eventEntity);

            // Main image
            multipartData.add("mainImage", new HttpEntity<>(mainImage.getResource()));

            // Gallery images
            for (MultipartFile file : galleryImages) {
                multipartData.add("galleryImages", new HttpEntity<>(file.getResource()));
            }

            return webClientBuilder.build()
                    .post()
                    .uri("lb://" + API_GATEWAY_SERVICE_NAME + ADMIN_EVENT_PATH)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(multipartData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(ResponseEntity::ok)
                    .onErrorResume(e -> Mono.just(ResponseEntity.status(500).body("Failed to add event: " + e.getMessage())));

        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(500).body("Exception occurred: " + e.getMessage()));
        }
    }
}