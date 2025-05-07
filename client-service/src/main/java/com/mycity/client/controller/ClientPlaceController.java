package com.mycity.client.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.mycity.shared.placedto.PlaceCategoryDTO;
import com.mycity.shared.placedto.PlaceDTO;
import com.mycity.shared.placedto.UserGalleryDTO;

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
	
	private static final String ADMIN_PLACE_ADDING_PATH="/admin/addPlace"; //With Images
	private static final String ADMIN_PLACE_ADD_PATH = "/admin/add-place";//With OutImages
	
    private static final String PLACE_ID_FINDING_PATH="/place/getid/{placeName}";
	
	private static final String PLACE_DETAILS_FINDING_PATH="/admin/getplace/{placeId}";
	
	private static final String PLACE_UPDATING_PATH="/admin/updateplace/{placeId}";
	
	private static final String PLACE_DELETING_PATH="/admin/deleteplace/{placeId}";
	
	private static final String PLACE_WITH_CATEGORIES_FINDING_PATH="/admin/getall/category";
	
	private static final String ALL_PLACES_FINDING_PATH="/place/getall";
	
	private static final String PATH_TO_ADD_IMAGES_TO_GALLERY="/place/gallery/upload";
	
		
	@PostMapping("/add")
	public Mono<String> addPlaceDetails(
	        @ModelAttribute PlaceDTO placeDto,
	        @RequestPart("images") List<MultipartFile> images,
	        @RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie) throws JsonProcessingException {

	    System.out.println("ClientPlaceController.addPlaceDetails()");
	    System.out.println("Place Name--------------->" + placeDto.getPlaceName());

	    //Extract JWT token from the cookie
	    String token = extractor.extractTokenFromCookie(cookie);

	    //using ObjectMapper to COnvert Java Object to JSON
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.registerModule(new JavaTimeModule());
	    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	    //Serialing the PlaceDTO into JSON
	    String placeDtoJson = objectMapper.writeValueAsString(placeDto);

	    //Build the multipart request
	    MultipartBodyBuilder builder = new MultipartBodyBuilder();
	    builder.part("placeDto", placeDtoJson)
	           .header("Content-Disposition", "form-data; name=placeDto")
	           .header("Content-Type", MediaType.APPLICATION_JSON_VALUE);

	    //Add image files
	    for (MultipartFile image : images) {
	        builder.part("images", image.getResource());
	    }

	    //Send request to Admin-Service via API-Gateway
	    return webClientBuilder.build()
	            .post()
	            .uri("lb://" + API_GATEWAY_SERVICE_NAME + ADMIN_PLACE_ADDING_PATH)
	            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
	            .contentType(MediaType.MULTIPART_FORM_DATA)
	            .body(BodyInserters.fromMultipartData(builder.build()))
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                clientResponse.bodyToMono(String.class)
	                        .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to Add Place: " + clientResponse.statusCode() + " - " + errorBody)))
	            )
	            .bodyToMono(String.class)
	            .onErrorResume(e -> {
	                e.printStackTrace();
	                return Mono.just("Failed to Add Place: " + e.getMessage());
	            });
	}


	@PostMapping("/addplace")
	public Mono<String> addPlace(@RequestBody PlaceDTO dto,@RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie)
	{
		System.out.println("ClientPlaceController.addPlace()");
		//using CookieTokenExtractor to Extract Token from Cookie
	    String token=extractor.extractTokenFromCookie(cookie);
		return webClientBuilder
			   .build()
			   .post()
			   .uri("lb://"+API_GATEWAY_SERVICE_NAME+ADMIN_PLACE_ADD_PATH)
			   .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
			   .bodyValue(dto)
			   .retrieve()
			   .onStatus(HttpStatusCode::isError, clientResponse ->
               clientResponse.bodyToMono(String.class)
                       .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to Add Place: " + clientResponse.statusCode() + " - " + errorBody)))
           )
		   .bodyToMono(String.class)
		   .onErrorResume(e->{
			  e.printStackTrace();
			  return Mono.just("Failed to Add Place: " + e.getMessage());
		   });
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
	public Mono<String> getPlaceDetailsById(@PathVariable Long placeId,@RequestHeader(value = HttpHeaders.COOKIE,required = false)String cookie) {
		System.out.println("ClientPlaceController.getPlaceDetailsById()");
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
	public Mono<String> updatePlace(@PathVariable  Long placeId,
			                        @RequestBody PlaceDTO dto,
			                        @RequestHeader(value = HttpHeaders.COOKIE,required = false)String cookie) 
	{
		System.out.println("ClientPlaceController.updatePlace()");
		//Extracting Token From Cookie
		String token=extractor.extractTokenFromCookie(cookie);
	    return webClientBuilder.build()
	            .put()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME + PLACE_UPDATING_PATH,placeId)
	            .body(Mono.just(dto), PlaceDTO.class)
	            .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to Update Place: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Failed to UPDATE Place: " + e.getMessage()));
	}
	
	@DeleteMapping("/deleteplace/{placeId}")
	public Mono<String> deletePlaceById(@PathVariable Long placeId,
			                            @RequestHeader(value = HttpHeaders.COOKIE,required = false)String cookie) 
	{
	    System.out.println("ClientPlaceController.deletePlaceById()");
	    //Extract Token from Cookie
	    String token=extractor.extractTokenFromCookie(cookie);
	    return webClientBuilder.build()
	            .delete()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME + PLACE_DELETING_PATH,placeId)
	            .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
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
	
	@GetMapping("/get/categories")
	public Mono<List<PlaceCategoryDTO>> getAllPlaceCategories(@RequestHeader(value = HttpHeaders.COOKIE,required = false) String cookie)
	{
		System.out.println("ClientPlaceController.getAllPlaceCategories()");
		// Extract Token from Cookie
		String token = extractor.extractTokenFromCookie(cookie);

		return webClientBuilder.build()
		        .get()
		        .uri("lb://" + API_GATEWAY_SERVICE_NAME + PLACE_WITH_CATEGORIES_FINDING_PATH)
		        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
		        .retrieve()
		        .onStatus(HttpStatusCode::isError, clientResponse ->
		                clientResponse.bodyToMono(String.class)
		                        .flatMap(errorBody -> Mono.error(new RuntimeException(
		                                "Failed to get Places Based on Category: " + clientResponse.statusCode() + " - " + errorBody))))
		        .bodyToMono(new ParameterizedTypeReference<List<PlaceCategoryDTO>>() {})
		        .onErrorResume(e -> {
		            System.err.println("Error occurred while Fetching Places: " + e.getMessage());
		            return Mono.just(Collections.emptyList());
		        });
	}	
	@PostMapping(value = "/gallery/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<String>> uploadImageToGallery(@RequestPart("image") List<MultipartFile> images,
	                                                         @ModelAttribute UserGalleryDTO dto,
	                                                         @RequestHeader(value=HttpHeaders.COOKIE,required = false) String cookie) {
		String token=extractor.extractTokenFromCookie(cookie);
	    return Mono.fromCallable(() -> {
	        MultipartBodyBuilder builder = new MultipartBodyBuilder();

	        // Add image parts
	        for (MultipartFile image : images) {
	            builder.part("images", new ByteArrayResource(image.getBytes()) {
	                @Override
	                public String getFilename() {
	                    return image.getOriginalFilename();
	                }
	            }).contentType(MediaType.parseMediaType(image.getContentType()));
	        }

	        // Add DTO part as JSON
	        builder.part("dto", dto).contentType(MediaType.APPLICATION_JSON);

	        // Send request to PLACE-SERVICE
	        String response = webClientBuilder.build()
	                .post()
	                .uri("lb://" + API_GATEWAY_SERVICE_NAME + PATH_TO_ADD_IMAGES_TO_GALLERY)
	                .contentType(MediaType.MULTIPART_FORM_DATA)
	                .bodyValue(builder.build())
	                .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
	                .retrieve()
	                .bodyToMono(String.class)
	                .block();

	        return ResponseEntity.ok(response);
	    });
	}
}
