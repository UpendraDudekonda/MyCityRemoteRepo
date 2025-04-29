package com.mycity.eventsplace.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.eventsplace.entity.Event;
import com.mycity.eventsplace.entity.EventHighlights;
import com.mycity.eventsplace.repository.EventsRepository;
import com.mycity.eventsplace.service.EventsPlaceServiceInterface;
import com.mycity.shared.eventsdto.EventCardDto;
import com.mycity.shared.eventsdto.EventsDTO;

import com.mycity.shared.mediadto.EventSubImagesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import com.mycity.eventsplace.serviceImpl.MediaServiceConfig;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EventsPlaceService implements EventsPlaceServiceInterface {

	
  private final EventsRepository eventRepo;

 
  private final  MediaServiceConfig mediaService;
    

    
    //adding events
    @Override
    public String addEvent(EventsDTO dto, List<MultipartFile> galleryImages,  List<String> imageNames) {
        if (dto.getEventName() == null || dto.getEventName().trim().isEmpty())
            throw new IllegalArgumentException("Enter Event Name");
        if (dto.getCity() == null || dto.getCity().trim().isEmpty())
            throw new IllegalArgumentException("Mention the City");
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty())
            throw new IllegalArgumentException("Mention the Event Description");
        if (dto.getDate() == null)
            throw new IllegalArgumentException("Mention the Event Date");
        if (dto.getDuration() == null)
            throw new IllegalArgumentException("Mention the Event Duration");
        if (dto.getEventPlaces() == null || dto.getEventPlaces().isEmpty())
            throw new IllegalArgumentException("Mention at least one Place related to the Event");
        if (dto.getSchedule() == null || dto.getSchedule().isEmpty())
            throw new IllegalArgumentException("Add at least one Event Highlight");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 
        LocalDate eventDate = LocalDate.parse(dto.getDate(), formatter);
        Event event = new Event();
        event.setEventName(dto.getEventName());
        event.setCity(dto.getCity());
        event.setDescription(dto.getDescription());
        event.setDate(eventDate);
        event.setDuration(dto.getDuration());
        event.setEventPlaces(dto.getEventPlaces());

       
       
        List<EventHighlights> highlights = dto.getSchedule().stream().map(highlightDto -> {
            EventHighlights highlight = new EventHighlights();
            highlight.setDate(eventDate);
            highlight.setTime(highlightDto.getTime());
            highlight.setActivityName(highlightDto.getActivityName());
           // set the event reference
            return highlight;
        }).collect(Collectors.toList());

        event.setSchedule(highlights);
        Event saved = eventRepo.save(event);



        EventSubImagesDTO galleryDto = new EventSubImagesDTO(null, null, saved.getEventName(), saved.getEventId(),null);
        mediaService.uploadGalleryImages(galleryImages, imageNames,galleryDto);

        return "Event '" + saved.getEventName() + "' saved with images!";
    }





    @Override
    public List<EventCardDto> getAllEventCards() {
        // TODO: Implement fetching event cards with main images
        return null;
    }

    

//updating 
    @Override
    public String updateEvent(Long eventId, EventsDTO dto, List<MultipartFile> galleryImages, List<String> imageNames) {
//         Validate basic event info
        if (dto.getEventName() == null || dto.getEventName().trim().isEmpty())
            throw new IllegalArgumentException("Enter Event Name");
        if (dto.getCity() == null || dto.getCity().trim().isEmpty())
            throw new IllegalArgumentException("Mention the City");
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty())
            throw new IllegalArgumentException("Mention the Event Description");
        if (dto.getDate() == null)
            throw new IllegalArgumentException("Mention the Event Date");
        if (dto.getDuration() == null)
            throw new IllegalArgumentException("Mention the Event Duration");
        if (dto.getEventPlaces() == null || dto.getEventPlaces().isEmpty())
            throw new IllegalArgumentException("Mention at least one Place related to the Event");
        if (dto.getSchedule() == null || dto.getSchedule().isEmpty())
            throw new IllegalArgumentException("Add at least one Event Highlight");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 
        LocalDate eventDate = LocalDate.parse(dto.getDate(), formatter);
        // Fetch existing event    
        Event existingEvent = eventRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        // Update fields
        existingEvent.setEventName(dto.getEventName());
        existingEvent.setCity(dto.getCity());
        existingEvent.setDescription(dto.getDescription());
        existingEvent.setDate(eventDate);
        existingEvent.setDuration(dto.getDuration());
        existingEvent.setEventPlaces(dto.getEventPlaces());

        // Replace highlights
        List<EventHighlights> highlights = dto.getSchedule().stream().map(highlightDto -> {
            EventHighlights highlight = new EventHighlights();
            highlight.setDate(eventDate);
            highlight.setTime(highlightDto.getTime());
            highlight.setActivityName(highlightDto.getActivityName());
            return highlight;
        }).collect(Collectors.toList());

        existingEvent.setSchedule(highlights);
        Event updatedEvent = eventRepo.save(existingEvent);

        // Upload new gallery images
        mediaService.updateEventImagesInMediaService(updatedEvent.getEventId(),updatedEvent.getEventName(),galleryImages, imageNames);

        return "Event '" + updatedEvent.getEventName() + "' updated with new images!";
    }

    
    
    //deleting 
    @Override
    public String deleteEvent(Long eventId) {
        // Check if event exists
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

        // Call media service to delete associated images
      mediaService.deleteEventImages(eventId, event.getEventName());
        // Delete event and related highlights
        eventRepo.delete(event);

        return "Event '" + event.getEventName() + "' deleted successfully with its images.";
    }



} 
