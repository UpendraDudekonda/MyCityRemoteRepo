package com.mycity.client.place;


import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.client.config.CookieTokenExtractor;
import com.mycity.shared.reviewdto.ReviewDTO;
import com.mycity.shared.reviewdto.ReviewSummaryDTO;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client/review")
@RequiredArgsConstructor
public class ClientPlaceReviewController 
{

	@Autowired
	private final WebClient.Builder webClientBuilder;
	
	@Autowired
	private final CookieTokenExtractor extractor;
	
	private static final String API_GATEWAY_SERVICE_NAME="API-GATEWAY";
	private static final String REVIEW_ADDING_PATH="/user/review/add";
	private static final String REVIEW_UPDATING_PATH="/user/review/update/{reviewId}";
	private static final String REVIEWS_GETTING_PATH="/user/review/get/{placeId}";
	private static final String REVIEWS_DELETING_PATH="/user/review/delete/{reviewId}";
	
	
	@PostMapping("/addreview")
	public Mono<String> addPlaceReview(
			            @RequestBody ReviewDTO dto,
			            @RequestHeader(value=HttpHeaders.COOKIE,required=false) String cookie) 
	{
		System.out.println("ClientPlaceReviewController.addPlaceReview()");
		//Extract Token From Cookie
		String token=extractor.extractTokenFromCookie(cookie);
	    return webClientBuilder.build()
	            .post()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME + REVIEW_ADDING_PATH)
	            .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
	            .body(Mono.just(dto), ReviewDTO.class)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to Add Review: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Failed to Add Review To The Place: " + e.getMessage()));
	}
	
	@PutMapping("/updatereview/{reviewId}")
	public Mono<String> updatePlaceReview(
			            @PathVariable Long reviewId,@RequestBody ReviewDTO dto,
			            @RequestHeader(value=HttpHeaders.COOKIE,required=false) String cookie) 
	{
		System.out.println("ClientPlaceReviewController.updatePlaceReview()");
		//Extract Token From Cookie
		String token=extractor.extractTokenFromCookie(cookie);
	    return webClientBuilder.build()
	            .put()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME + REVIEW_UPDATING_PATH,reviewId)
	            .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
	            .body(Mono.just(dto), ReviewDTO.class)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to Update Review: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Failed to Update Review: " + e.getMessage()));
	}

	
	@GetMapping("/getreview/{placeId}")
	public Mono<List<ReviewSummaryDTO>> getReviewsByPlaceId(
	        @PathVariable Long placeId,
	        @RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie) 
	{
	    System.out.println("ClientPlaceReviewController.getPlacesByReviewId()");

	    // Extract Token From Cookie
	    String token = extractor.extractTokenFromCookie(cookie);

	    return webClientBuilder.build()
	            .get()
	            .uri("lb://" + API_GATEWAY_SERVICE_NAME + REVIEWS_GETTING_PATH, placeId)
	            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException(
	                                "Failed to Get Reviews: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToFlux(ReviewSummaryDTO.class)
	            .collectList()
	            .onErrorResume(e -> {
	                System.err.println("Error: " + e.getMessage());
	                return Mono.just(Collections.emptyList());
	            });
	}
	
	@DeleteMapping("/deletereview/{reviewId}")
	public Mono<String> deleteReviewsByPlaceId(
	        @PathVariable Long reviewId,
	        @RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie) 
	{
	    System.out.println("ClientPlaceReviewController.deleteReviewsByreviewId()");

	    // Extract Token From Cookie
	    String token = extractor.extractTokenFromCookie(cookie);

	    return webClientBuilder.build()
	            .delete()
	            .uri("lb://" + API_GATEWAY_SERVICE_NAME + REVIEWS_DELETING_PATH, reviewId)
	            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException(
	                                "Failed to Delete Reviews: " + clientResponse.statusCode() + " - " + errorBody)))
	            )
	            .bodyToMono(String.class)
	            .onErrorResume(e -> {
	                System.err.println("Error: " + e.getMessage());
	                return Mono.just("Failed to Delete reviews: " + e.getMessage());
	            });
	}


}
