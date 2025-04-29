package com.mycity.place.serviceImpl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.mycity.shared.reviewdto.ReviewDTO;

@Service
public class WebClientReviewService {

    @Autowired
    private WebClient.Builder webClientBuilder;
    

    // Define constants for review service URL and paths
    private static final String REVIEW_SERVICE = "REVIEW-SERVICE";  // The service name in the load balancer or registry
    private static final String REVIEW_FETCH_PATH = "/review/place-reviews/{placeId}";  // The endpoint to fetch reviews

    // Method to fetch reviews for a specific place from the REVIEW-SERVICE
    public CompletableFuture<List<ReviewDTO>> fetchReviews(Long placeId) {
        // Using WebClient to call the REVIEW-SERVICE and fetch the reviews for the given placeId
        return webClientBuilder.baseUrl("lb://" + REVIEW_SERVICE)  // Load-balanced URI to call the REVIEW-SERVICE
                .build()
                .get()
                .uri(REVIEW_FETCH_PATH, placeId)  // Replace the {placeId} in the path with the actual value
                .retrieve()
                .bodyToFlux(ReviewDTO.class)  // Assuming the response is a list of ReviewDTO objects
                .collectList()  // Collect all reviews into a List
                .toFuture();  // Return the result as a CompletableFuture
    }

    // Optional: Method to handle errors if needed (you can add custom error handling based on your needs)
    private void handleError(WebClientResponseException e) {
        // Log the error or handle it appropriately
        System.err.println("Error occurred while fetching reviews: " + e.getMessage());
    }
}
