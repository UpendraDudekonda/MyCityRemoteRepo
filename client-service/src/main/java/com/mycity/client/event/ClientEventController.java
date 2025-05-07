package com.mycity.client.event;

import java.time.format.DateTimeFormatter;
import java.util.List;

import java.util.Map;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.core.ParameterizedTypeReference;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


import com.mycity.client.config.MultipartInputStreamFileResource;

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
    private static final String ADMIN_EVENT_PATH = "/admin/event/add";

    private static final String UPDATE_PATH = "/admin/event/update/";
    private static final String DELETE_PATH = "/admin/event/delete/";
    private static final String EVENTS_FETCH_DETAILS="/event/internal/fetch/";
    private static final String EVENTS_FETCH_CARTS="/event/internal/fetch";
    


    @PostMapping(value = "/event/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> addEvent(
            @RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie,
            @ModelAttribute EventsDTO eventDTO, 
            @RequestParam(name="imageNames", required=false) List<String> imageNames,
            @RequestPart("galleryImages") List<MultipartFile> galleryImages) {

        String token = cookieTokenExtractor.extractTokenFromCookie(cookie);
        if (token == null || token.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("Authorization token is missing"));
        }

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // Add fields from eventDTO individually
            if (eventDTO.getEventName() != null) body.add("eventName", eventDTO.getEventName());
            if (eventDTO.getDate() != null) body.add("date", eventDTO.getDate());
            if (eventDTO.getDuration() != null) {
                body.add("duration", eventDTO.getDuration().format(formatter));  // Custom format
            }
            if (eventDTO.getDescription() != null) body.add("description", eventDTO.getDescription());
            if (eventDTO.getCity() != null) body.add("city", eventDTO.getCity());

            // If eventPlaces is a List<String>, add each event place as separate value
            if (eventDTO.getEventPlaces() != null) {
                for (String place : eventDTO.getEventPlaces()) {
                    body.add("eventPlaces", place);
                }
            }

            // If schedule is List<ScheduleDTO>, add each schedule item individually
            if (eventDTO.getSchedule() != null) {
                for (int i = 0; i < eventDTO.getSchedule().size(); i++) {
                    var sched = eventDTO.getSchedule().get(i);
                    body.add("schedule[" + i + "].date", sched.getDate());
                    body.add("schedule[" + i + "].time", sched.getTime().format(formatter));
                    body.add("schedule[" + i + "].activityName", sched.getActivityName());
                }
            }

            // Add gallery images
            for (MultipartFile file : galleryImages) {
                body.add("galleryImages", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
            }

            // Add image names if provided
            for (String name : imageNames) {
                body.add("imageNames", name);
            }

            // Make the request to the API Gateway (which forwards to the admin service)
            return webClientBuilder.build()
                    .post()
                    .uri("lb://" + API_GATEWAY_SERVICE_NAME + ADMIN_EVENT_PATH)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(ResponseEntity::ok)
                    .onErrorResume(e -> Mono.just(ResponseEntity.status(500).body("Failed to add event: " + e.getMessage())));

        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(500).body("Exception occurred: " + e.getMessage()));
        }
    }

    
    
    @PutMapping(value = "/event/update/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> updateEvent(
            @RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie,
            @PathVariable Long eventId,
            @ModelAttribute EventsDTO eventDTO,
            @RequestParam(name = "imageNames", required = false) List<String> imageNames,
            @RequestPart(name = "galleryImages", required = false) List<MultipartFile> galleryImages) {

        String token = cookieTokenExtractor.extractTokenFromCookie(cookie);
        if (token == null || token.isEmpty()) {
            return Mono.just(ResponseEntity.status(401).body("Unauthorized: Missing token"));
        }

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            if (eventDTO.getEventName() != null) body.add("eventName", eventDTO.getEventName());
            if (eventDTO.getDate() != null) body.add("date", eventDTO.getDate());
            if (eventDTO.getDuration() != null) body.add("duration", eventDTO.getDuration().toString());
            if (eventDTO.getDescription() != null) body.add("description", eventDTO.getDescription());
            if (eventDTO.getCity() != null) body.add("city", eventDTO.getCity());

            if (eventDTO.getEventPlaces() != null) {
                for (String place : eventDTO.getEventPlaces()) {
                    body.add("eventPlaces", place);
                }
            }

            if (eventDTO.getSchedule() != null) {
                for (int i = 0; i < eventDTO.getSchedule().size(); i++) {
                    var sched = eventDTO.getSchedule().get(i);
                    body.add("schedule[" + i + "].date", sched.getDate());
                    body.add("schedule[" + i + "].time", sched.getTime().toString());
                    body.add("schedule[" + i + "].activityName", sched.getActivityName());
                }
            }

            if (galleryImages != null) {
                for (MultipartFile file : galleryImages) {
                    body.add("galleryImages", new com.mycity.client.config.MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
                }
            }

            if (imageNames != null) {
                for (String name : imageNames) {
                    body.add("imageNames", name);
                }
            }

            return webClientBuilder.build()
                    .put()
                    .uri("lb://" + API_GATEWAY_SERVICE_NAME + UPDATE_PATH + eventId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(ResponseEntity::ok)
                    .onErrorResume(e -> Mono.just(ResponseEntity.status(500).body("Update failed: " + e.getMessage())));

        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(500).body("Exception: " + e.getMessage()));
        }
    }

    // ✅ Delete Event
    @DeleteMapping("/event/delete/{eventId}")
    public Mono<ResponseEntity<String>> deleteEvent(
            @RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie,
            @PathVariable Long eventId) {

        String token = cookieTokenExtractor.extractTokenFromCookie(cookie);
        if (token == null || token.isEmpty()) {
            return Mono.just(ResponseEntity.status(401).body("Unauthorized: Missing token"));
        }

        return webClientBuilder.build()
                .delete()
                .uri("lb://" + API_GATEWAY_SERVICE_NAME + DELETE_PATH + eventId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500).body("Delete failed: " + e.getMessage())));
    }


    
    @GetMapping("/event/fetch/{eventId}")
    public Mono<ResponseEntity<Map<String, Object>>> fetchEventDetails(
            @PathVariable Long eventId){
    
    
        return webClientBuilder.build()
                .get()
                .uri("lb://" + API_GATEWAY_SERVICE_NAME + EVENTS_FETCH_DETAILS + eventId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to fetch event details: " + e.getMessage()))));
    }

    @GetMapping("/event/fetch")
    public Mono<ResponseEntity<Map<String, Object>>> fetchEventCarts(){

        return webClientBuilder.build()
                .get()
                .uri("lb://" + API_GATEWAY_SERVICE_NAME + EVENTS_FETCH_CARTS)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to fetch event carts: " + e.getMessage()))));
    }




}
