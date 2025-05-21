package com.mycity.user.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycity.shared.reviewdto.ReviewDTO;
import com.mycity.shared.reviewdto.ReviewSummaryDTO;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user/review")
public class UserReviewController {

	@Autowired
	private WebClient.Builder webClientBuilder;

	private static final String REVIEW_SERVICE_NAME = "REVIEW-SERVICE";
	private static final String REVIEW_ADDING_PATH = "/review/addreview";
	private static final String REVIEW_UPDATING_PATH = "/review/updatereview/{reviewId}";
	private static final String REVIEWS_GETTING_PATH = "/review/getreviews/{placeId}";
	private static final String REVIEW_DELETING_PATH = "/review/deletereview/{reviewId}";

	@PostMapping(path = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addReview(
	        @RequestPart("dto") String dtoJson,
	        @RequestPart("images") List<MultipartFile> images) throws JsonProcessingException {

	    System.out.println("=====UserReviewController.addReview()====");

	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.registerModule(new JavaTimeModule());
	    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	    ReviewDTO dto = objectMapper.readValue(dtoJson, ReviewDTO.class);

	    System.out.println("From USER --> Review for Place ::" + dto.getPlaceName());

	    MultipartBodyBuilder builder = new MultipartBodyBuilder();
	    builder.part("dto", dtoJson)
	           .header("Content-Type", MediaType.APPLICATION_JSON_VALUE);

	    for (MultipartFile image : images) {
	        builder.part("images", image.getResource());
	    }

	    // Use exchange() to access status code
	    ClientResponse response = webClientBuilder.build()
	            .post()
	            .uri("lb://" + REVIEW_SERVICE_NAME + REVIEW_ADDING_PATH)
	            .contentType(MediaType.MULTIPART_FORM_DATA)
	            .body(BodyInserters.fromMultipartData(builder.build()))
	            .exchange()
	            .block(); // still using block

	    if (response != null) {
	        HttpStatusCode status = response.statusCode();
	        String body = response.bodyToMono(String.class).block();

	        return ResponseEntity.status(status).body(body);
	    } else {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Failed to connect to Review Service");
	    }
	}

	@GetMapping("/get/{placeId}")
	public ResponseEntity<List<ReviewSummaryDTO>> getReviewByPlaceId(@PathVariable Long placeId) {
		System.out.println("UserReviewController.getPlacesByReviewId()");
		List<ReviewSummaryDTO> reviewList = webClientBuilder.build().get()
				.uri("lb://" + REVIEW_SERVICE_NAME + REVIEWS_GETTING_PATH, placeId).retrieve()
				.onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
						.flatMap(errorBody -> Mono.error(new RuntimeException(
								"Failed to fetch Reviews: " + clientResponse.statusCode() + " - " + errorBody))))
				.bodyToFlux(ReviewSummaryDTO.class).collectList().onErrorResume(e -> {
					System.err.println("Error: " + e.getMessage());
					return Mono.just(Collections.emptyList());
				}).block();

		return new ResponseEntity<>(reviewList, HttpStatus.OK);
	}

	@DeleteMapping("/delete/{reviewId}")
	public ResponseEntity<String> deleteReviewsByreviewId(@PathVariable Long reviewId) {
		System.out.println("-------UserReviewController.deleteReviewsByreviewId()------");
		String result = webClientBuilder.build().delete()
				.uri("lb://" + REVIEW_SERVICE_NAME + REVIEW_DELETING_PATH, reviewId).retrieve()
				.onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
						.flatMap(errorBody -> Mono.error(new RuntimeException(
								"Failed to delete review: " + clientResponse.statusCode() + " - " + errorBody))))
				.bodyToMono(String.class).onErrorResume(e -> {
					System.err.println("Error: " + e.getMessage());
					return Mono.just("Failed to delete review: " + e.getMessage());
				}).block();

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	// GetUserReviews()

	// UpdateReview

	// DeleteReview
}
