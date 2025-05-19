package com.mycity.eventsplace;



import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycity.eventsplace.config.SecurityConfig;
import com.mycity.eventsplace.controller.EventsPlaceController;
import com.mycity.eventsplace.service.EventsPlaceServiceInterface;
import com.mycity.shared.eventsdto.EventHighlightsDTO;
import com.mycity.shared.eventsdto.EventsDTO;

@WebMvcTest(EventsPlaceController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class EventsPlaceControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventsPlaceServiceInterface eventsPlaceService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testAddEvent() throws Exception {
        EventsDTO dto = new EventsDTO("Music Night", LocalTime.of(2, 0), "A fun event", LocalTime.of(18, 0), "Mumbai", "2025-05-20",
                Collections.singletonList(new EventHighlightsDTO("2025-05-20", LocalTime.of(18, 0), "DJ Start")));

        MockMultipartFile image = new MockMultipartFile("galleryImages", "img1.jpg", MediaType.IMAGE_JPEG_VALUE, "fake-image".getBytes());
//        MockMultipartFile eventJson = new MockMultipartFile("eventDTO", "", "application/json", objectMapper.writeValueAsBytes(dto));

        when(eventsPlaceService.addEvent(any(), any(), any())).thenReturn("Event added");

        mockMvc.perform(multipart("/event/internal/add")
                        .file(image)
                        .param("imageNames", "img1.jpg")
                        .param("eventName", dto.getEventName())
                        .param("duration", "02:00")
                        .param("description", dto.getDescription())
                        .param("city", dto.getCity())
                        .param("date", dto.getDate())
                        .param("schedule[0].date", "2025-05-20")
                        .param("schedule[0].time", "18:00")
                        .param("schedule[0].activityName", "DJ Start")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("Event added"));
    }

    @Test
    void testUpdateEvent() throws Exception {
        Long eventId = 1L;
        EventsDTO dto = new EventsDTO("Music Night", LocalTime.of(2, 0), "Updated desc", LocalTime.of(18, 0), "Delhi", "2025-05-22",
                Collections.singletonList(new EventHighlightsDTO("2025-05-22", LocalTime.of(18, 0), "Dance Show")));

        MockMultipartFile image = new MockMultipartFile("galleryImages", "img2.jpg", MediaType.IMAGE_JPEG_VALUE, "fake-image".getBytes());

        when(eventsPlaceService.updateEvent(anyLong(), any(), any(), any())).thenReturn("Event updated");

        mockMvc.perform(multipart("/event/internal/update/{eventId}", eventId)
                        .file(image)
                        .param("imageNames", "img2.jpg")
                        .param("eventName", dto.getEventName())
                        .param("duration", "02:00")
                        .param("description", dto.getDescription())
                        .param("city", dto.getCity())
                        .param("date", dto.getDate())
                        .param("schedule[0].date", "2025-05-22")
                        .param("schedule[0].time", "18:00")
                        .param("schedule[0].activityName", "Dance Show")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("Event updated"));
    }

    @Test
    void testDeleteEvent() throws Exception {
        Long eventId = 1L;
        when(eventsPlaceService.deleteEvent(eventId)).thenReturn("Event deleted");

        mockMvc.perform(delete("/event/internal/delete/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(content().string("Event deleted"));
    }

    @Test
    void testFetchEventDetails() throws Exception {
        Long eventId = 1L;
        Map<String, Object> details = new HashMap<>();
        details.put("sectionId", "eventDetails");
        details.put("data", Map.of("eventName", "Test Event"));

        when(eventsPlaceService.createEventDetailsSection(eventId)).thenReturn(details);

        mockMvc.perform(get("/event/internal/fetch/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sectionId").value("eventDetails"));
    }

    @Test
    void testFetchEventCarts() throws Exception {
        Map<String, Object> carts = new HashMap<>();
        carts.put("sectionId", "eventCart");
        carts.put("data", List.of(Map.of("eventName", "Event 1")));

        when(eventsPlaceService.createEventCartSection()).thenReturn(carts);

        mockMvc.perform(get("/event/internal/fetch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sectionId").value("eventCart"));
    }
}
