package com.mycity.client.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.client.config.CookieTokenExtractor;
import com.mycity.shared.placedto.PlaceDTO;
import com.mycity.shared.placedto.PlaceDiscoveriesDTO;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client/discovery")
public class ClientAdminPlaceDiscoveriesController 
{
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	@Autowired
	private CookieTokenExtractor extractor;
	
	private static final String API_GATEWAY_SERVICE_NAME="API-GATEWAY";
	private static final String PATH_TO_ADD_PLACE_TO_DISCOVERY="/admin/discoveries/addPlace";
	private static final String PATH_GET_LIST_OF_PLACES="/admin/discoveries/getallPlaces";
	private static final String PATH_TO_GET_PLACE_DETAILS="/admin/discoveries/getPlace/{placeName}";
	
	@PostMapping("/addplace")
    public Mono<ResponseEntity<String>> AddPlaceToDiscovery(@RequestBody PlaceDiscoveriesDTO dto,@RequestHeader(value=HttpHeaders.COOKIE,required = false) String cookie) 
 	{
 		System.out.println("ClientAdminPlaceDiscoverController.AddPlaceToDiscovery()");
 	    //Extract Token From Cookie
 		String token=extractor.extractTokenFromCookie(cookie);
        return webClientBuilder.build()
                .post()
                .uri("lb://" +API_GATEWAY_SERVICE_NAME +PATH_TO_ADD_PLACE_TO_DISCOVERY)
                .bodyValue(dto)
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
                .retrieve()
                .toEntity(String.class)
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(response.getBody()));
    }
	
	@GetMapping("/getall")
    public Mono<ResponseEntity<List<PlaceDiscoveriesDTO>>> getAllPlaces(@RequestHeader(value=HttpHeaders.COOKIE,required = false) String cookie) 
 	{
 		System.out.println("ClientAdminPlaceDiscoverController.getAllPlaces()");
 	    //Extract Token From Cookie
 		String token=extractor.extractTokenFromCookie(cookie);
        return webClientBuilder.build()
                .get()
                .uri("lb://" +API_GATEWAY_SERVICE_NAME +PATH_GET_LIST_OF_PLACES)
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<PlaceDiscoveriesDTO>>() {})
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(response.getBody()));
    }
	
	@GetMapping("/getplace/{placeName}")
	public Mono<ResponseEntity<PlaceDTO>> getPlaceByName(@RequestHeader(value=HttpHeaders.COOKIE,required = false) String cookie,@PathVariable String placeName)
	{
		System.out.println("ClientAdminPlaceDiscoveriesController.getPlaceByName()");
		//Extract Token From Token Extractor
		String token=extractor.extractTokenFromCookie(cookie);
		return webClientBuilder.build()
				.get()
				.uri("lb://"+API_GATEWAY_SERVICE_NAME+PATH_TO_GET_PLACE_DETAILS,placeName)
				.header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
				.retrieve()
				.toEntity(PlaceDTO.class)
				.map(response -> ResponseEntity.status(response.getStatusCode()).body(response.getBody()));
	}
}
