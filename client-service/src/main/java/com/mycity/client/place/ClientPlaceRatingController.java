package com.mycity.client.place;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.client.config.CookieTokenExtractor;
import com.mycity.shared.ratingdto.RatingDTO;
 

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client/rating")
public class ClientPlaceRatingController 
{
	
	@Autowired
	private  WebClient.Builder webClientBuilder;
	
	@Autowired
	private CookieTokenExtractor extractor;
	
	private static final String API_GATEWAY_SERVICE_NAME="API-GATEWAY";
	private static final String USER_RATING_ADDING_PATH="/user/rating/add";
	
	@PostMapping("/addrating")
	public Mono<String> addPlaceRating(
			            @RequestBody RatingDTO dto,
			            @RequestHeader(value=HttpHeaders.COOKIE,required=false) String cookie) 
	{
		System.out.println("ClientPlaceRatingController.addPlaceRating()");
		//Extract Token From Cookie
		String token=extractor.extractTokenFromCookie(cookie);
	    return webClientBuilder.build()
	            .post()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME +USER_RATING_ADDING_PATH,cookie)
	            .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
	            .bodyValue(dto)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to Add Rating: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Failed to Add Rating To The Place: " + e.getMessage()));
	}
}
