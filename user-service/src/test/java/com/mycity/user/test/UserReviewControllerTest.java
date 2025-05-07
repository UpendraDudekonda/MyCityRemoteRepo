package com.mycity.user.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycity.shared.reviewdto.ReviewDTO;

import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureMockMvc
class UserReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Mockito.when(webClientBuilder.build()).thenReturn(webClient);
        Mockito.when(webClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.contentType(Mockito.any())).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.body(Mockito.any(), Mockito.any(Class.class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void testAddReview_Success() throws Exception {
        ReviewDTO reviewDTO = new ReviewDTO(1L, 2L, 3L, "Test Place", "TestUser", "Amazing experience!", LocalDate.now());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String dtoJson = objectMapper.writeValueAsString(reviewDTO);
        MockMultipartFile dtoPart = new MockMultipartFile("dto", "", "application/json", dtoJson.getBytes());

        MockMultipartFile imageFile = new MockMultipartFile("images", "review.jpg", "image/jpeg", "dummy-image".getBytes());

        Mockito.when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Review added"));

        mockMvc.perform(multipart("/add")
                        .file(dtoPart)
                        .file(imageFile)
                      //  .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Review added"));
    }

    @Test
    void testAddReview_Failure() throws Exception {
        ReviewDTO reviewDTO = new ReviewDTO(1L, 2L, 3L, "Test Place", "TestUser", "Bad experience", LocalDate.now());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String dtoJson = objectMapper.writeValueAsString(reviewDTO);
        MockMultipartFile dtoPart = new MockMultipartFile("dto", "", "application/json", dtoJson.getBytes());

        MockMultipartFile imageFile = new MockMultipartFile("images", "review.jpg", "image/jpeg", "dummy-image".getBytes());

        Mockito.when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Failed to Add Review"));

        mockMvc.perform(multipart("/add")
                        .file(dtoPart)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Failed to Add Review"));
    }
}



