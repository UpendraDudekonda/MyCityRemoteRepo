package com.mycity.client.place;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
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
import com.mycity.shared.placedto.PlaceDTO;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client/place")
@RequiredArgsConstructor
public class ClientPlaceController
{
	@Autowired
	private final WebClient.Builder webClientBuilder;
	@Autowired
	private CookieTokenExtractor extractor;
	
	private static final String API_GATEWAY_SERVICE_NAME="API-GATEWAY";
	
	private static final String PLACE_REGISTRATION_PATH="/place/addplace";
	
    private static final String PLACE_ID_FINDING_PATH="/place/getid/{placeName}";
	
	private static final String PLACE_DETAILS_FINDING_PATH="/place/getplace/{placeId}";
	
	private static final String PLACE_UPDATING_PATH="/place/updateplace/{placeId}";
	
	private static final String PLACE_DELETING_PATH="/place/deleteplace/{placeId}";
	
	private static final String ALL_PLACES_FINDING_PATH="/place/getall";
	
		
	@PostMapping("/add")
	public Mono<String> addPlace(@RequestBody PlaceDTO dto,@RequestHeader(value = HttpHeaders.COOKIE)String cookie) 
	{
		System.out.println("ClientPlaceController.addPlace()");
		//extracting token from cookie
		String token=extractor.extractTokenFromCookie(cookie);
	    return webClientBuilder.build()
	            .post()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME + PLACE_REGISTRATION_PATH)
	            .body(Mono.just(dto), PlaceDTO.class)
	            .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to Add Place: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Failed to Add Place: " + e.getMessage()));
	}
	

	@GetMapping("/getid/{placeName}")
	public Mono<String> getPlaceId(@PathVariable String placeName) {
		System.out.println("ClientPlaceController.getPlaceId()");
	    return webClientBuilder.build()
	            .get()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME + PLACE_ID_FINDING_PATH,placeName)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to Add Place: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Failed to get Place Id: " + e.getMessage()));
	}
	
	@GetMapping("/getplace/{placeId}")
	public Mono<String> getPlaceDetailsById(@PathVariable Long placeId,@RequestHeader(value = HttpHeaders.COOKIE)String cookie) {
		System.out.println("ClientPlaceController.addPlace()");
		String token=extractor.extractTokenFromCookie(cookie);
	    return webClientBuilder.build()
	            .get()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME + PLACE_DETAILS_FINDING_PATH,placeId)
	            .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to get Place Details: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Failed to get Place Details: " + e.getMessage()));
	}
	
	@PutMapping("/update/{placeId}")
	public Mono<String> updatePlace(@PathVariable  Long placeId,@RequestBody PlaceDTO dto) 
	{
		System.out.println("ClientPlaceController.updatePlace()");
	    return webClientBuilder.build()
	            .put()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME + PLACE_UPDATING_PATH,placeId)
	            .body(Mono.just(dto), PlaceDTO.class)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to Update Place: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Failed to UPDATE Place: " + e.getMessage()));
	}
	
	@DeleteMapping("/deleteplace/{placeId}")
	public Mono<String> deletePlaceById(@PathVariable Long placeId) 
	{
	    System.out.println("ClientPlaceController.deletePlaceById()");
	    return webClientBuilder.build()
	            .delete()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME + PLACE_DELETING_PATH,placeId)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to Delete Place: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Failed to Delete Place: " + e.getMessage()));
	}
	
	@GetMapping("/getallplaces")
	public Mono<List<PlaceDTO>> getAllPlaces() 
	{
	    System.out.println("......ClientPlaceController.getAllPlaces().....");
	    //String token=extractor.extractTokenFromCookie(cookie);
	    //System.out.println("Token ..............->"+token);
	    return webClientBuilder.build()
	            .get()
	            .uri("lb://" + API_GATEWAY_SERVICE_NAME + ALL_PLACES_FINDING_PATH)
	            //.header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to get List of Place Details: " + clientResponse.statusCode() + " - " + errorBody)))
	            )
	            .bodyToMono(new ParameterizedTypeReference<List<PlaceDTO>>() {})
	            .onErrorResume(e -> {
	                System.err.println("Error occurred: " + e.getMessage());
	                return Mono.just(Collections.emptyList()); // or return Mono.error(e) if you want to propagate the error
	            });
	}
}
