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
    private static final String MEDIA_FETCH_PATH = "/media/bycategory/image/{category}"; // Path variable

        public Mono<String> fetchCategoryImage(String categoryName) {
            System.err.println("Request is passing from category to media");
            System.out.println("Fetching image URL for category: " + categoryName);

            return webClientBuilder
                    .build()
                    .get()
                    .uri("lb://" + MEDIA_SERVICE + MEDIA_FETCH_PATH, categoryName)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnTerminate(() -> System.out.println("Request completed for category: " + categoryName))
                    .doOnSuccess(imageUrl -> System.out.println("Received image URL: " + imageUrl))
                    .doOnError(error -> {
                        System.err.println("Error calling media-service: " + error.getMessage());
                        error.printStackTrace();
                    });
        }

}
