package com.mycity.eventsplace.serviceImpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.Set;
import java.util.HashSet;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.eventsplace.entity.Event;
import com.mycity.eventsplace.entity.EventHighlights;
import com.mycity.eventsplace.repository.EventsRepository;
import com.mycity.eventsplace.service.EventsPlaceServiceInterface;
import com.mycity.shared.eventsdto.EventsDTO;
import com.mycity.shared.mediadto.EventSubImagesDTO;
import com.mycity.shared.placedto.AboutPlaceEventDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventsPlaceService implements EventsPlaceServiceInterface {

	
  private final EventsRepository eventRepo;

 
  
  private final  MediaServiceConfig mediaService;
    

    
    //adding events
    @Override
    public String addEvent(EventsDTO dto, List<MultipartFile> galleryImages,  List<String> imageNames) {
    	validateEventDTO(dto);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 
        LocalDate eventDate = LocalDate.parse(dto.getDate(), formatter);
        Event event = new Event();
        event.setEventName(dto.getEventName());
        event.setCity(dto.getCity());
        event.setDescription(dto.getDescription());
        event.setDate(eventDate);
        event.setDuration(dto.getDuration());
//        event.setEventPlaces(dto.getEventPlaces());

       
       
        List<EventHighlights> highlights = dto.getSchedule().stream().map(highlightDto -> {
            EventHighlights highlight = new EventHighlights();
            highlight.setDate(eventDate);
            highlight.setTime(highlightDto.getTime());
            highlight.setActivityName(highlightDto.getActivityName());
           // set the event reference
            return highlight;
        }).collect(Collectors.toList());

        event.setSchedule(highlights);
//        if (eventRepo.existsByEventName(dto.getEventName())) {
//            throw new IllegalArgumentException("An event with this name already exists.");
//        }
        Event saved = eventRepo.save(event);



        EventSubImagesDTO galleryDto = new EventSubImagesDTO(null, null, saved.getEventName(), saved.getEventId(),null);
        mediaService.uploadGalleryImages(galleryImages, imageNames,galleryDto);

        return "Event '" + saved.getEventName() + "' saved with images!";
    }






//updating 
    @Override
    public String updateEvent(Long eventId, EventsDTO dto, List<MultipartFile> galleryImages, List<String> imageNames) {
//         Validate basic event info
    	validateEventDTO(dto);
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
//        existingEvent.setEventPlaces(dto.getEventPlaces());

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

   
    //validations
    private void validateEventDTO(EventsDTO dto) {
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
        if (dto.getSchedule() == null || dto.getSchedule().isEmpty())
            throw new IllegalArgumentException("Add at least one Event Highlight");
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


    
  
    
   @Override
    public Map<String, Object> createEventDetailsSection(Long eventId) {
    	
    	 Event selectedEvent = eventRepo.findById(eventId).orElse(null);
         if (selectedEvent == null) {
             return null; // or throw exception
         }

         List<String> images = new ArrayList<>();
         try {
             images = mediaService.getImageUrlsForEvent(eventId);
         } catch (Exception e) {
            System.out.println("feching event images: {}" +e.getMessage());
         }
        Map<String, Object> detailsSection = new HashMap<>();
        detailsSection.put("sectionId", "eventDetails");

            Map<String, Object> data = new HashMap<>();
            data.put("eventName", selectedEvent.getEventName());
            data.put("description", selectedEvent.getDescription());
            data.put("date", selectedEvent.getDate());
            data.put("time", selectedEvent.getDuration()); // assuming "duration" means "time"
           
//            data.put("eventPlaces", selectedEvent.getEventPlaces());
  
            data.put("schedule", selectedEvent.getSchedule()); // List<EventHighlights>
            data.put("eventImages", images); // Optional: Use event-specific images if available

            //similarEvents
            List<Map<String, Object>> matchingCarts = new ArrayList<>();
            List<Event> allEvents = eventRepo.findAll();
            for (Event ev : allEvents) {
                if (ev.getEventName().equalsIgnoreCase(selectedEvent.getEventName())
                        && !ev.getEventId().equals(eventId)) { // exclude the current one if needed
                    Map<String, Object> cartData = new HashMap<>();
                    cartData.put("eventName", ev.getEventName());
                    cartData.put("date", ev.getDate());
                    cartData.put("city", ev.getCity());

                    try {
                        List<String> cartImages = mediaService.getImageUrlsForEvent(ev.getEventId());
                        cartData.put("cartImages", cartImages);
                    } catch (Exception ex) {
                        System.out.println("Failed to fetch images for cart event " + ev.getEventId());
                    }

                    matchingCarts.add(cartData);
                }
            }

            data.put("similarEvents", matchingCarts); 

        detailsSection.put("data", data);
     
    
		return detailsSection;
    }


    @Override
   	public Map<String, Object> createEventCartSection() {
    	 List<Event> events = eventRepo.findAll(); 
           Map<String, Object> cartSection = new HashMap<>();
           cartSection.put("sectionId", "eventCart");

         
           List<Map<String, Object>> eventList = new ArrayList<>();
           Set<String> seenEventNames = new HashSet<>(); 
           
           for (Event eventsList : events) {
        	   String eventName = eventsList.getEventName();
        	   
        	   if (seenEventNames.contains(eventName)) {
                   continue; // Skip duplicates
               }
               seenEventNames.add(eventName); 
           Map<String, Object> data = new HashMap<>();
           data.put("eventName",eventName);
           data.put("date", eventsList.getDate());
         // Directly a list of strings
           data.put("city", eventsList.getCity());
           data.put("eventId",eventsList.getEventId());
          
    // assuming Event has getPlace()
           try {
               List<String> images = mediaService.getImageUrlsForEvent(eventsList.getEventId());
               data.put("images", images);
           } catch (Exception ex) {
               System.out.println("Failed to fetch images for event " + eventsList.getEventId());
           }
          
           eventList.add(data);
    
       }
           cartSection.put("data",eventList);
   		return cartSection;
           
       }






	@Override
	public List<AboutPlaceEventDTO> fetchEvents(Long placeId) {
		// TODO Auto-generated method stub
		return null;
	}





} 
