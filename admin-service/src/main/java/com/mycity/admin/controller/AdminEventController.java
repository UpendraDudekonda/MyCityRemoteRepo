package com.mycity.admin.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
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

import com.mycity.admin.config.MultipartInputStreamFileResource;
import com.mycity.shared.eventsdto.EventsDTO;
import com.mycity.shared.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminEventController {

    private static final String EVENT_SERVICE_NAME = "eventsplace-service";
    private static final String ADD_EVENT_PATH = "/event/internal/add";
    private static final String UPDATE_EVENT_PATH = "/event/internal/update/";
    private static final String DELETE_EVENT_PATH = "/event/internal/delete/";

    @Autowired
    private final WebClient.Builder webClientBuilder;

    @PostMapping(value = "/event/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ApiResponse<String>>> addEvent(
            @RequestHeader("Authorization") String authHeader,
            @ModelAttribute EventsDTO eventDTO,
            @RequestParam(name = "imageNames", required = false) List<String> imageNames,
            @RequestPart("galleryImages") List<MultipartFile> galleryImages
    ) {
        String finalToken = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            if (eventDTO.getEventName() != null) body.add("eventName", eventDTO.getEventName());
            if (eventDTO.getDate() != null) body.add("date", eventDTO.getDate());
            if (eventDTO.getDuration() != null) body.add("duration", eventDTO.getDuration().format(formatter));
            if (eventDTO.getDescription() != null) body.add("description", eventDTO.getDescription());
            if (eventDTO.getCity() != null) body.add("city", eventDTO.getCity());

            if (eventDTO.getSchedule() != null) {
                for (int i = 0; i < eventDTO.getSchedule().size(); i++) {
                    var sched = eventDTO.getSchedule().get(i);
                    body.add("schedule[" + i + "].date", sched.getDate());
                    body.add("schedule[" + i + "].time", sched.getTime().format(formatter));
                    body.add("schedule[" + i + "].activityName", sched.getActivityName());
                }
            }

            for (MultipartFile file : galleryImages) {
                body.add("galleryImages", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
            }

            if (imageNames != null) {
                for (String name : imageNames) {
                    body.add("imageNames", name);
                }
            }

            return webClientBuilder.build()
                    .post()
                    .uri("lb://" + EVENT_SERVICE_NAME + ADD_EVENT_PATH)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + finalToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(bodyResponse -> ResponseEntity.ok(new ApiResponse<>(200, "Event added successfully", bodyResponse)))
                    .onErrorResume(e -> Mono.just(ResponseEntity.status(500)
                            .body(new ApiResponse<>(500, "Failed to add event", e.getMessage()))));

        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(500)
                    .body(new ApiResponse<>(500, "Exception occurred", e.getMessage())));
        }
    }

    @PutMapping(value = "/event/update/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ApiResponse<String>>> updateEvent(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long eventId,
            @ModelAttribute EventsDTO eventDTO,
            @RequestParam(name = "imageNames", required = false) List<String> imageNames,
            @RequestPart(name = "galleryImages", required = false) List<MultipartFile> galleryImages
    ) {
        String finalToken = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            if (eventDTO.getEventName() != null) body.add("eventName", eventDTO.getEventName());
            if (eventDTO.getDate() != null) body.add("date", eventDTO.getDate());
            if (eventDTO.getDuration() != null) body.add("duration", eventDTO.getDuration().format(formatter));
            if (eventDTO.getDescription() != null) body.add("description", eventDTO.getDescription());
            if (eventDTO.getCity() != null) body.add("city", eventDTO.getCity());

            if (eventDTO.getSchedule() != null) {
                for (int i = 0; i < eventDTO.getSchedule().size(); i++) {
                    var sched = eventDTO.getSchedule().get(i);
                    body.add("schedule[" + i + "].date", sched.getDate());
                    body.add("schedule[" + i + "].time", sched.getTime().format(formatter));
                    body.add("schedule[" + i + "].activityName", sched.getActivityName());
                }
            }

            if (galleryImages != null) {
                for (MultipartFile file : galleryImages) {
                    body.add("galleryImages", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
                }
            }

            if (imageNames != null) {
                for (String name : imageNames) {
                    body.add("imageNames", name);
                }
            }

            return webClientBuilder.build()
                    .put()
                    .uri("lb://" + EVENT_SERVICE_NAME + UPDATE_EVENT_PATH + eventId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + finalToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(resp -> ResponseEntity.ok(new ApiResponse<>(200, "Event updated successfully", resp)))
                    .onErrorResume(e -> Mono.just(ResponseEntity.status(500)
                            .body(new ApiResponse<>(500, "Update failed", e.getMessage()))));

        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(500)
                    .body(new ApiResponse<>(500, "Exception occurred", e.getMessage())));
        }
    }

    @DeleteMapping("/event/delete/{eventId}")
    public Mono<ResponseEntity<ApiResponse<String>>> deleteEvent(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long eventId
    ) {
        String finalToken = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;

        return webClientBuilder.build()
                .delete()
                .uri("lb://" + EVENT_SERVICE_NAME + DELETE_EVENT_PATH + eventId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + finalToken)
                .retrieve()
                .bodyToMono(String.class)
                .map(resp -> ResponseEntity.ok(new ApiResponse<>(200, "Event deleted successfully", resp)))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500)
                        .body(new ApiResponse<>(500, "Delete failed", e.getMessage()))));
    }
}
