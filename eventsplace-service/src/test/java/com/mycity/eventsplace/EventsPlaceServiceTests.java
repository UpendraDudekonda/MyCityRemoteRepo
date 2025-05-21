package com.mycity.eventsplace;

import com.mycity.eventsplace.entity.Event;
import com.mycity.eventsplace.entity.EventHighlights;
import com.mycity.eventsplace.repository.EventsRepository;
import com.mycity.eventsplace.serviceImpl.EventsPlaceService;
import com.mycity.eventsplace.serviceImpl.MediaServiceConfig;
import com.mycity.shared.eventsdto.EventsDTO;
import com.mycity.shared.eventsdto.EventHighlightsDTO;
import com.mycity.shared.mediadto.EventSubImagesDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class EventsPlaceServiceTests {

	
    @Mock
    private EventsRepository eventRepo;

    
    @Mock
    private MediaServiceConfig mediaService;

    @InjectMocks
    private EventsPlaceService eventsPlaceService;

    private EventsDTO mockEventDTO;

    @BeforeEach
    void setup() {
        mockEventDTO = new EventsDTO();
        mockEventDTO.setEventName("Music Fest");
        mockEventDTO.setCity("New York");
        mockEventDTO.setDescription("Live Music & Food");
        mockEventDTO.setDate("2025-07-01");
        mockEventDTO.setDuration(LocalTime.of(2, 30)); // 2:30 hours
        mockEventDTO.setTime(LocalTime.of(18, 0)); // 6 PM start

        EventHighlightsDTO highlight = new EventHighlightsDTO();
        highlight.setActivityName("Band Performance");
        highlight.setTime(LocalTime.of(18, 30));
        highlight.setDate("2025-07-01");

        mockEventDTO.setSchedule(List.of(highlight));
    }

    @Test
    void testAddEventSuccess() {
        Event savedEvent = new Event();
        savedEvent.setEventId(1L);
        savedEvent.setEventName("Music Fest");

        when(eventRepo.save(any(Event.class))).thenReturn(savedEvent);

        String result = eventsPlaceService.addEvent(mockEventDTO, new ArrayList<>(), new ArrayList<>());

        assertEquals("Event 'Music Fest' saved with images!", result);
        verify(mediaService).uploadGalleryImages(anyList(), anyList(), any(EventSubImagesDTO.class));
    }

    @Test
    void testUpdateEventSuccess() {
        Event existingEvent = new Event();
        existingEvent.setEventId(1L);
        existingEvent.setEventName("Old Event");

        when(eventRepo.findById(1L)).thenReturn(Optional.of(existingEvent));
        when(eventRepo.save(any(Event.class))).thenReturn(existingEvent);

        String result = eventsPlaceService.updateEvent(1L, mockEventDTO, new ArrayList<>(), new ArrayList<>());

        assertEquals("Event 'Music Fest' updated with new images!", result);
        verify(mediaService).updateEventImagesInMediaService(eq(1L), eq("Music Fest"), anyList(), anyList());
    }

    @Test
    void testDeleteEventSuccess() {
        Event event = new Event();
        event.setEventId(1L);
        event.setEventName("Music Fest");

        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));

        String result = eventsPlaceService.deleteEvent(1L);

        assertEquals("Event 'Music Fest' deleted successfully with its images.", result);
        verify(mediaService).deleteEventImages(eq(1L), eq("Music Fest"));
        verify(eventRepo).delete(event);
    }

    @Test
    void testCreateEventDetailsSection_ById() {
        Event event = new Event();
        event.setEventId(1L);
        event.setEventName("Music Fest");
        event.setDescription("Live Music & Food");
        event.setDate(LocalDate.of(2025, 7, 1));
        event.setDuration(LocalTime.of(2, 30));

        EventHighlights highlight = new EventHighlights();
        highlight.setActivityName("Band Performance");
        highlight.setTime(LocalTime.of(18, 30));
        event.setSchedule(List.of(highlight));

        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepo.findAll()).thenReturn(List.of(event));
        when(mediaService.getImageUrlsForEvent(1L)).thenReturn(List.of("img1.jpg"));

        Map<String, Object> result = eventsPlaceService.createEventDetailsSection(1L);

        assertNotNull(result);
        assertEquals("eventDetails", result.get("sectionId"));

        Map<String, Object> data = (Map<String, Object>) result.get("data");
        assertEquals("Music Fest", data.get("eventName"));
        assertTrue(((List<?>) data.get("eventImages")).contains("img1.jpg"));
    }

    @Test
    void testCreateEventCartSection() {
        Event event = new Event();
        event.setEventId(1L);
        event.setEventName("Music Fest");
        event.setDate(LocalDate.of(2025, 7, 1));
        event.setCity("New York");

        when(eventRepo.findAll()).thenReturn(List.of(event));
        when(mediaService.getImageUrlsForEvent(1L)).thenReturn(List.of("img1.jpg", "img2.jpg"));

        Map<String, Object> result = eventsPlaceService.createEventCartSection();

        assertEquals("eventCart", result.get("sectionId"));
        List<?> data = (List<?>) result.get("data");
        assertEquals(1, data.size());

        Map<?, ?> firstEntry = (Map<?, ?>) data.get(0);
        assertEquals("Music Fest", firstEntry.get("eventName"));
        assertTrue(((List<?>) firstEntry.get("images")).contains("img1.jpg"));
    }
}
