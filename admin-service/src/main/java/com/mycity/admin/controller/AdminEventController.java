package com.mycity.admin.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycity.admin.config.MultipartInputStreamFileResource;
import com.mycity.shared.eventsdto.EventsDTO;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminEventController {

//	
//	- addEvent(@RequestBody EventDto eventDto): Adds a new event.

	
	private static final String API_GATEWAY_SERVICE_NAME = "EVENTSPLACE-SERVICE";
    private static final String ADMIN_EVENT_PATH = "/event/internal/add";
  private final WebClient.Builder webClientBuilder;
	   
  @PostMapping(value = "/events/add", consumes = "multipart/form-data")
  public ResponseEntity<?> addEvent(
          @RequestPart("event") EventsDTO eventsDTO,
          @RequestPart("mainImage") MultipartFile mainImage,
          @RequestPart("galleryImages") MultipartFile[] galleryImages,
          @RequestHeader("X-User-Id") Long userId) {

      try {
          MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
          body.add("event", new ObjectMapper().writeValueAsString(eventsDTO));
          body.add("mainImage", new MultipartInputStreamFileResource(mainImage.getInputStream(), mainImage.getOriginalFilename()));
          for (MultipartFile file : galleryImages) {
              body.add("galleryImages", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
          }

          String response = webClientBuilder.build()
                  .post()
                  .uri("http://localhost:8090/event/internal/add")
                  .contentType(MediaType.MULTIPART_FORM_DATA)
                  .bodyValue(body)
                  .retrieve()
                  .bodyToMono(String.class)
                  .block();

          return ResponseEntity.ok(response);
      } catch (Exception e) {
          return ResponseEntity.badRequest().body("Error while adding event: " + e.getMessage());
      }
  }


//	- updateEvent(@PathVariable Long eventId, @RequestBody EventDto eventDto): Updates an event.
//	- deleteEvent(@PathVariable Long eventId): Deletes an event.


}
