package com.mycity.client.placetest;

import com.mycity.client.place.ClientAboutPlaceDetailController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

@SpringBootTest
public class ClientAboutPlaceDetailControllerTest {

    @InjectMocks
    private ClientAboutPlaceDetailController controller;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    // Use raw types to avoid generic capture issues
    @SuppressWarnings("rawtypes")
	@Mock
    private WebClient.RequestHeadersUriSpec uriSpec;

    @SuppressWarnings("rawtypes")
	@Mock
    private WebClient.RequestHeadersSpec headersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(webClientBuilder.build()).thenReturn(webClient);
        Mockito.when(webClient.get()).thenReturn(uriSpec);
    }

    @Test
    public void testGetPlaceDetails_Success() {
        Long placeId = 1L;
        Map<String, Object> mockResponse = Map.of("placeName", "City Park");

        Mockito.when(uriSpec.uri(Mockito.anyString(), Mockito.eq(placeId))).thenReturn(headersSpec);
        Mockito.when(headersSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToMono(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(mockResponse));

        Mono<ResponseEntity<Map<String, Object>>> result = controller.getPlaceDetails(placeId);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getStatusCode().is2xxSuccessful() &&
                        response.getBody() != null &&
                        "City Park".equals(response.getBody().get("placeName"))
                )
                .verifyComplete();
    }

    @Test
    public void testGetPlaceDetails_NotFound() {
        Long placeId = 2L;

        Mockito.when(uriSpec.uri(Mockito.anyString(), Mockito.eq(placeId))).thenReturn(headersSpec);
        Mockito.when(headersSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToMono(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.empty());

        Mono<ResponseEntity<Map<String, Object>>> result = controller.getPlaceDetails(placeId);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getStatusCodeValue() == 404 &&
                        response.getBody() != null &&
                        "Place not found".equals(response.getBody().get("error")))
                .verifyComplete();
    }
}
