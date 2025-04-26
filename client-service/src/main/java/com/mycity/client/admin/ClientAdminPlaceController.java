package com.mycity.client.admin;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import com.mycity.client.config.CookieTokenExtractor;
import com.mycity.shared.admindto.AdminPlaceResponseDTO;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client/admin")
public class ClientAdminPlaceController 
{
	@Autowired
	private  WebClient.Builder webClientBuilder;
	
	@Autowired
	private CookieTokenExtractor extractor;
	
	private static final String API_GATEWAY_SERVICE_NAME="API-GATEWAY";
	private static final String PATH_TO_GET_ALL_PLACES="/admin/getallplaces";
	
 	@GetMapping("/getallplaces")
    public Mono<ResponseEntity<List<AdminPlaceResponseDTO>>> getAllPlaces(@RequestHeader(value=HttpHeaders.COOKIE,required = false) String cookie) 
 	{
 		System.out.println("ClientAdminPlaceController.getAllPlaces()");
 	    //Extract Token From Cookie
 		String token=extractor.extractTokenFromCookie(cookie);
        return webClientBuilder.build()
                .get()
                .uri("lb://" +API_GATEWAY_SERVICE_NAME +PATH_TO_GET_ALL_PLACES)
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<AdminPlaceResponseDTO>>() {})
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(response.getBody()));
    }
}
