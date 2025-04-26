package com.mycity.category.serviceImpl;

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
    

    public Mono<String> fetchCategoryImage(String categoryName) {
        return webClientBuilder.baseUrl("lb://" + MEDIA_SERVICE)  
            .build()
            .get()
            .uri(MEDIA_FETCH_PATH + categoryName)  
            .retrieve()
            .bodyToMono(String.class);
    }
}
