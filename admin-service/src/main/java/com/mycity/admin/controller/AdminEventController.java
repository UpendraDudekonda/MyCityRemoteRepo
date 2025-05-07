package com.mycity.admin.controller;


import java.time.format.DateTimeFormatter;
import java.util.List;

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

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminEventController {

    private static final String EVENT_SERVICE_NAME = "EVENTSPLACE-SERVICE";
    private static final String ADMIN_EVENT_PATH = "/event/internal/add";
    private static final String UPDATE_EVENT_PATH = "/event/internal/update/";
    private static final String DELETE_EVENT_PATH = "/event/internal/delete/";
    private final WebClient.Builder webClientBuilder;

    @PostMapping(value = "/event/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addEvent(
            @RequestHeader("Authorization") String authHeader,
            @ModelAttribute EventsDTO eventDTO,
            @RequestParam(name = "imageNames", required = false) List<String> imageNames,
            @RequestPart("galleryImages") List<MultipartFile> galleryImages
    ) {
        // Extract the token from the Authorization header
        String finalToken = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
        
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

            // Make the request to the event service
            String response = webClientBuilder.build()
                    .post()
                    .uri("http://localhost:8090/event/internal/add") // Ensure this is correct
                    .header("Authorization", "Bearer " + finalToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to route event creation: " + e.getMessage());
        }
    }
    
    @PutMapping(value = "/event/update/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateEvent(
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

            if (eventDTO.getEventPlaces() != null) {
                for (String place : eventDTO.getEventPlaces()) {
                    body.add("eventPlaces", place);
                }
            }

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

            String response = webClientBuilder.build()
                    .put()
                    .uri("http://localhost:8090/event/internal/update/" + eventId)
                    .header("Authorization", "Bearer " + finalToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to update event: " + e.getMessage());
        }
    }

    @DeleteMapping("/event/delete/{eventId}")
    public ResponseEntity<?> deleteEvent(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long eventId
    ) {
        String finalToken = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;

        try {
            String response = webClientBuilder.build()
                    .delete()
                    .uri("http://localhost:8090/event/internal/delete/"+ eventId)
                    .header("Authorization", "Bearer " + finalToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to delete event: " + e.getMessage());
        }
    }

}