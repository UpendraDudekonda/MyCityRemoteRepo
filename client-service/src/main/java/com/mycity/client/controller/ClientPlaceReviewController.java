package com.mycity.client.controller;


import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
	
	
	@PostMapping(path = "/addreview",
		    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
		    produces = MediaType.APPLICATION_JSON_VALUE)
		public Mono<String> addPlaceReview(
		        @ModelAttribute("dto") ReviewDTO dto,
		        @RequestPart("images") List<MultipartFile> images,
		        @RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie) throws JsonProcessingException {

		    System.out.println("ClientPlaceReviewController.addPlaceReview()");
		    System.out.println("Reviewing Place: " + dto.getPlaceName());

		    // Extract JWT token from cookie
		    String token = extractor.extractTokenFromCookie(cookie);

		    // Serialize ReviewDTO to JSON
		    ObjectMapper objectMapper = new ObjectMapper();
		    objectMapper.registerModule(new JavaTimeModule());
		    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		    String reviewDtoJson = objectMapper.writeValueAsString(dto);

		    // Build multipart request
		    MultipartBodyBuilder builder = new MultipartBodyBuilder();
		    builder.part("dto", reviewDtoJson)
		           .header("Content-Disposition", "form-data; name=dto")
		           .header("Content-Type", MediaType.APPLICATION_JSON_VALUE);

		    for (MultipartFile image : images) {
		        builder.part("images", image.getResource());
		    }

		    // Send request to Review Service via API Gateway
		    return webClientBuilder.build()
		            .post()
		            .uri("lb://" + API_GATEWAY_SERVICE_NAME + REVIEW_ADDING_PATH)
		            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
		            .contentType(MediaType.MULTIPART_FORM_DATA)
		            .body(BodyInserters.fromMultipartData(builder.build()))
		            .retrieve()
		            .onStatus(HttpStatusCode::isError, clientResponse ->
		                clientResponse.bodyToMono(String.class)
		                        .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to Add Review: " +
		                                clientResponse.statusCode() + " - " + errorBody))))
		            .bodyToMono(String.class)
		            .onErrorResume(e -> {
		                e.printStackTrace();
		                return Mono.just("Failed to Add Review To The Place: " + e.getMessage());
		            });
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
