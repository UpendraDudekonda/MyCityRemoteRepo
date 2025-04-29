package com.mycity.eventsplace.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.eventsplace.service.EventsPlaceServiceInterface;

import com.mycity.shared.eventsdto.EventsDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventsPlaceController {

    private final EventsPlaceServiceInterface eventsPlaceService;

    // Create - Add Event
    @PostMapping(value = "/internal/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addEvent(
            @ModelAttribute EventsDTO eventDTO,
            @RequestParam(name = "imageNames", required = false) List<String> imageNames,
            @RequestPart("galleryImages") List<MultipartFile> galleryImages
    ) {
        System.out.println("Inside EventsPlaceController: " + eventDTO.getEventName());
        String response = eventsPlaceService.addEvent(eventDTO, galleryImages, imageNames);
        return ResponseEntity.ok(response);
    }

//    // Read - Get all events for main page
//    @GetMapping("/main-page")
//    public ResponseEntity<List<EventCardDto>> getEventsForMainPage() {
//        return ResponseEntity.ok(eventsPlaceService.getMainPageEvents());
//    }
//
//    // Read - Get event details by ID
//    @GetMapping("/{eventId}/details")
//    public ResponseEntity<EventHighlightsDTO> getEventDetails(@PathVariable String eventId) {
//        return ResponseEntity.ok(eventsPlaceService.getEventDetails(eventId));
//    }

    // Update - Update an event
    @PutMapping(value = "/internal/update/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateEvent(
            @PathVariable Long eventId,
            @ModelAttribute EventsDTO eventDTO,
            @RequestParam(name = "imageNames", required = false) List<String> imageNames,
            @RequestPart(name = "galleryImages", required = false) List<MultipartFile> galleryImages
    ) {
        String response = eventsPlaceService.updateEvent(eventId, eventDTO, galleryImages, imageNames);
        return ResponseEntity.ok(response);
    }

    // Delete - Delete an event by ID
    @DeleteMapping("/internal/delete/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventId) {
        String response = eventsPlaceService.deleteEvent(eventId);
        return ResponseEntity.ok(response);
    }
}
