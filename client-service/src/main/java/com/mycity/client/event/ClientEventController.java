package com.mycity.client.event;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
}
