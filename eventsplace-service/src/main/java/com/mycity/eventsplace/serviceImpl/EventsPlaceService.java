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
import com.mycity.shared.mediadto.EventMainImageDTO;
import com.mycity.shared.mediadto.EventSubImagesDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventsPlaceService implements EventsPlaceServiceInterface {

    private final EventsRepository eventRepo;
    private final WebClient.Builder webClientBuilder;

    private static final String MEDIA_BASE_URL = "http://localhost:8094";

    @Override
    public String addEvent(EventsDTO dto, MultipartFile mainImageFile, List<MultipartFile> galleryImages) {
        if (dto.getName() == null || dto.getName().trim().isEmpty())
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

        Event event = new Event();
        event.setEventName(dto.getName());
        event.setCity(dto.getCity());
        event.setDescription(dto.getDescription());
        event.setDate(dto.getDate());
        event.setDuration(dto.getDuration());
        event.setEventPlaces(dto.getEventPlaces());

       

       
        
        List<EventHighlights> highlights = dto.getSchedule().stream().map(highlightDto -> {
            EventHighlights highlight = new EventHighlights();
            highlight.setDate(highlightDto.getDate());
            highlight.setTime(highlightDto.getTime());
            highlight.setActivityName(highlightDto.getActivityName());
           // set the event reference
            return highlight;
        }).collect(Collectors.toList());

        event.setSchedule(highlights);
        Event saved = eventRepo.save(event);

        EventMainImageDTO mainImageDto = new EventMainImageDTO(null, null, saved.getEventName(), saved.getEventId());
        uploadMainImage(mainImageFile, mainImageDto);

        EventSubImagesDTO galleryDto = new EventSubImagesDTO(null, null, saved.getEventName(), saved.getEventId());
        uploadGalleryImages(galleryImages, galleryDto);

        return "Event '" + saved.getEventName() + "' saved with images!";
    }

    private void uploadMainImage(MultipartFile mainImage, EventMainImageDTO dto) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", mainImage.getResource());
        body.add("eventId", dto.getEventId());
        body.add("eventName", dto.getEventName());

        webClientBuilder.build()
                .post()
                .uri(MEDIA_BASE_URL + "/upload/image")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private void uploadGalleryImages(List<MultipartFile> galleryImages, EventSubImagesDTO dto) {
        for (MultipartFile file : galleryImages) {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("files", file.getResource());
            body.add("eventId", dto.getEventId());
            body.add("eventName", dto.getEventName());

            webClientBuilder.build()
                    .post()
                    .uri(MEDIA_BASE_URL + "/upload/images")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }
    }

    @Override
    public List<EventCardDto> getAllEventCards() {
        // TODO: Implement fetching event cards with main images
        return null;
    }


} 
