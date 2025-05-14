package com.mycity.category.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class WebClientMediaService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    
    private static final String MEDIA_SERVICE = "MEDIA-SERVICE";  
    private static final String MEDIA_FETCH_PATH = "/media/bycategory/image?category="; 
    private static final String MEDIA_FETCHBY_PLACE_PATH = "/media/findby/place";
    

    public Mono<String> fetchCategoryImage(String categoryName) {
        return webClientBuilder.baseUrl("lb://" + MEDIA_SERVICE)  
            .build()
            .get()
            .uri(MEDIA_FETCH_PATH + categoryName)  
            .retrieve()
            .bodyToMono(String.class);
    }
    public Mono<List<String>> getImagesByPlaceId(Long placeId) {
        return webClientBuilder.baseUrl("lb://" + MEDIA_SERVICE)
            .build()
            .get()
            .uri(uriBuilder -> uriBuilder.path(MEDIA_FETCHBY_PLACE_PATH)
                                        .queryParam("placeId", placeId)
                                        .build())
            .retrieve()
            .bodyToFlux(String.class)  // Assuming the response is a list of image URLs (String)
            .collectList();
    }
}
