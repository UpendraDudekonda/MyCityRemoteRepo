package com.mycity.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.ratingdto.RatingDTO;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRatingController 
{
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	private static final String RATING_SERVICE_NAME="rating-service";
	private static final String RATING_ADDING_PATH="/rating/add";
	
    @PostMapping("/rating/add")
    public ResponseEntity<String> addRating(@RequestBody RatingDTO dto)
    {
	   //using webClient to route from user-service TO rating-service
        System.out.println("UserRatingController.addRating()");
		String result = webClientBuilder.build()
			    .post()
			    .uri("lb://"+RATING_SERVICE_NAME+RATING_ADDING_PATH)
			    .bodyValue(dto)
			    .retrieve()
			    .onStatus(HttpStatusCode::isError, clientResponse ->
			        clientResponse.bodyToMono(String.class)
			            .flatMap(errorBody -> Mono.error(new RuntimeException(
			                "Failed to Add Rating to the Place-->: " + clientResponse.statusCode() + " - " + errorBody)))
			    )
			    .bodyToMono(String.class)
			    .onErrorResume(e -> Mono.just("Failed to Add Rating to the  Place-->: " + e.getMessage()))
			    .block(); // 
		return new ResponseEntity<String>(result,HttpStatus.OK);
    }
}
