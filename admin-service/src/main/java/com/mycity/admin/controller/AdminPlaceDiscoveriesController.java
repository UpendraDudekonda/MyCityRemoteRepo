package com.mycity.admin.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.placedto.PlaceDTO;
import com.mycity.shared.placedto.PlaceDiscoveriesDTO;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin/discoveries")
public class AdminPlaceDiscoveriesController 
{
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	private static final String PLACE_SERVICE_NAME="place-service";
	private static final String PATH_TO_ADD_PLACE_TO_DISCOVERIES="/place/discoveries/add";
	private static final String PATH_TO_GET_ALL_DISCOVERIES="/place/discoveries/getall";
	private static final String PATH_TO_GET_SELECTED_PLACE_DETAILS="/place/discoveries/getplace/{placeName}";
	
   @PostMapping("/addPlace")
   public ResponseEntity<String> addPlace(@RequestBody PlaceDiscoveriesDTO dto)
   {
	   System.out.println("AdminPlaceDiscoveriesController.addPlace()");
	   // Use webClientBuilder to route from admin --> place-service
	   String result = webClientBuilder.build()
			    .post()
			    .uri("lb://" + PLACE_SERVICE_NAME + PATH_TO_ADD_PLACE_TO_DISCOVERIES)
			    .bodyValue(dto)
			    .retrieve()
			    .onStatus(HttpStatusCode::isError, clientResponse ->
			        clientResponse.bodyToMono(String.class)
			            .flatMap(errorBody -> Mono.error(new RuntimeException(
			                "Failed to Add Place to Discoveries: " + clientResponse.statusCode() + " - " + errorBody)))
			    )
			    .bodyToMono(String.class)
			    .onErrorResume(e -> Mono.just("Failed to Add Place to Discoveries : " + e.getMessage()))
			    .block(); // <- Get the result synchronously
	   return new ResponseEntity<String>(result,HttpStatus.OK);
   }
   
   @GetMapping("/getallPlaces")
   public ResponseEntity<List<PlaceDiscoveriesDTO>> getAllPlaces()
   {
	   System.out.println("AdminPlaceDiscoveriesController.getAllPlaces()");
	   // Use webClientBuilder to route from admin --> place-service
	   List<PlaceDiscoveriesDTO> places = webClientBuilder.build()
			    .get()
			    .uri("lb://" + PLACE_SERVICE_NAME +PATH_TO_GET_ALL_DISCOVERIES )
			    .retrieve()
			    .onStatus(HttpStatusCode::isError, clientResponse ->
			        clientResponse.bodyToMono(String.class)
			            .flatMap(errorBody -> Mono.error(new RuntimeException(
			                "Failed to get List of Places: " + clientResponse.statusCode() + " - " + errorBody)))
			    )
			    .bodyToMono(new ParameterizedTypeReference<List<PlaceDiscoveriesDTO>>() {})
			    .onErrorResume(e -> {
		            // Log error and return an empty list instead of error
		            System.err.println("Error fetching discoveries: " + e.getMessage());
		            return Mono.just(Collections.emptyList());
		        })
		        .block();
	   return new ResponseEntity<List<PlaceDiscoveriesDTO>>(places,HttpStatus.FOUND);
   }
   
   @GetMapping("/getPlace/{placeName}")
   public ResponseEntity<PlaceDTO> getAllPlaces(@PathVariable String placeName)
   {
	   System.out.println("AdminPlaceDiscoveriesController.getAllPlaces()");
	   // Use webClientBuilder to route from admin --> place-service
	   PlaceDTO place = webClientBuilder.build()
			    .get()
			    .uri("lb://" + PLACE_SERVICE_NAME + PATH_TO_GET_SELECTED_PLACE_DETAILS,placeName)
			    .retrieve()
			    .onStatus(HttpStatusCode::isError, clientResponse ->
			        clientResponse.bodyToMono(String.class)
			            .flatMap(errorBody -> Mono.error(new RuntimeException(
			                "Failed to get Place Details: " + clientResponse.statusCode() + " - " + errorBody)))
			    )
			    .bodyToMono(PlaceDTO.class)
			    .onErrorResume(e -> {
			        // Log error and return null instead of throwing
			        System.err.println("Error fetching place Details: " + e.getMessage());
			        return Mono.just(new PlaceDTO()); 
			    })
			    .block();

	   return new ResponseEntity<PlaceDTO>(place,HttpStatus.FOUND);
   }
 
}
