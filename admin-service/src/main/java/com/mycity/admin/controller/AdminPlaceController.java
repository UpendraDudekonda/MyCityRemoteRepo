package com.mycity.admin.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycity.shared.admindto.AdminPlaceResponseDTO;
import com.mycity.shared.placedto.PlaceCategoryDTO;
import com.mycity.shared.placedto.PlaceDTO;
import com.mycity.shared.placedto.PlaceResponseDTO;


@RestController
@RequestMapping("/admin")
public class AdminPlaceController 
{
	
	private static final String PLACE_SERVICE_NAME="place-service";
	private static final String PATH_TO_GET_LIST_OF_PLACES="/place/allplaces";
	private static final String PATH_TO_ADD_PLACE_WITH_IMAGES="/place/add-place";
	private static final String PATH_TO_GET_PLACE="/place/get/{placeId}";
	private static final String PATH_TO_UPDATE_PLACE="/place/update/{placeId}";
	private static final String PATH_TO_DELETE_PLACE="/place/delete/{placeId}";
	private static final String PATH_TO_GET_PLACE_IDS_AND_CATEGORIES="/place/places/categories";
	
	@Autowired
	public WebClient.Builder webClientBuilder;

//	- `addPlace(@RequestBody PlaceDTO placeDTO)` : Adds a new place.
//	- `updatePlace(@PathVariable Long placeId, @RequestBody PlaceDTO placeDTO)` : Updates an existing place.
//	- `deletePlace(@PathVariable Long placeId)` : Deletes a place.
//	- `approvePlace(@PathVariable Long placeId)` : Approves a place (if moderation is required).
//	- `getAllPlacesForAdmin(Pageable pageable, @RequestParam(required = false) String category, @RequestParam(required = false) String searchQuery)` : Retrieves a paginated list of all places for admin, with optional category filtering and search.
//	- `getPendingPlaces()` : Retrieves a list of places awaiting approval.
//	- `getReportedPlaces()` : Retrieves a list of reported places.
//	- `getPlaceReport(@PathVariable Long placeId)` : Retrieves reports for a specific place.
//	- `addPlaceImage(@PathVariable Long placeId, @RequestBody PlaceImageDTO imageDTO)` : Adds an image to a place.
//	- `deletePlaceImage(@PathVariable Long placeId, @PathVariable Long imageId)` : Deletes an image from a place.
//	- `linkPlaceToGoogleMaps(@PathVariable Long placeId, @RequestParam String googlePlaceId)` : Links a place to a Google Maps Place ID.
//	- `fetchAndStoreGooglePlaceDetails(@PathVariable Long placeId)` : Fetches and stores details from Google Maps API.
//	- `addPlaceEvent(@PathVariable Long placeId, @RequestBody EventDTO eventDTO)` : adds an event to a place.
//	- `removePlaceEvent(@PathVariable Long placeId, @PathVariable Long eventId)` : removes an event from a place.
//	- `addPlaceListing(@PathVariable Long placeId, @RequestBody ListingDTO listingDTO)` : adds a listing to a place.
//	- `removePlaceListing(@PathVariable Long placeId, @PathVariable Lo`
	
	@GetMapping("/getallplaces")
	public ResponseEntity<List<AdminPlaceResponseDTO>> getAllPlacesForAdmin()
	{
		System.out.println("AdminPlaceController.getAllPlacesForAdmin()");
		     List<PlaceResponseDTO> places= webClientBuilder
		    		.build()
		            .get()
		            .uri("lb://"+PLACE_SERVICE_NAME+PATH_TO_GET_LIST_OF_PLACES)
		            .retrieve()
		            .bodyToMono(new ParameterizedTypeReference<List<PlaceResponseDTO>>() {})
		            .block();
		     
		     List<AdminPlaceResponseDTO> dtos=new ArrayList<AdminPlaceResponseDTO>();
		     for(PlaceResponseDTO place:places)
		     {
	             //Copying data from PlaceDTO to AdminPlaceResponseDTO...to display only name,Time,image,posted on details
		    	 AdminPlaceResponseDTO response=new AdminPlaceResponseDTO();
		    	 response.setCurrentDate(LocalDate.now());
		    	 response.setImageUrl("");
		    	 response.setPlaceName(place.getPlaceName());
		    	 response.setLocation("");
		    	 
		    	 System.out.println("Admin Response Data====> ::"+response);
		    	 //add response to List
		    	 dtos.add(response);
		     }
		     
		     return new ResponseEntity<List<AdminPlaceResponseDTO>>(dtos,HttpStatus.OK); 
	}
	
	@PostMapping(path = "/addPlace",
	        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
	        produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addPlaceDetails(
	        @RequestPart PlaceDTO placeDto,
	        @RequestPart("images") List<MultipartFile> images) throws JsonProcessingException {

	    System.out.println("AdminPlaceController.addPlaceDetails()");
	    System.out.println("From ADMIN--->PlaceName ::" + placeDto.getPlaceName());

	    //Using ObjectMapper to Convert Java Object to JSON
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.registerModule(new JavaTimeModule());
	    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	    String placeDtoJson = objectMapper.writeValueAsString(placeDto);

	    MultipartBodyBuilder builder = new MultipartBodyBuilder();
	    builder.part("placeDto", placeDtoJson)
	           .header("Content-Type", MediaType.APPLICATION_JSON_VALUE);

	    // Add the images to the builder
	    for (MultipartFile image : images) {
	        builder.part("images", image.getResource());
	    }

	    //Use WebClient to send the request to Place-Service
	    String response = webClientBuilder.build()
	            .post()
	            .uri("lb://"+PLACE_SERVICE_NAME+PATH_TO_ADD_PLACE_WITH_IMAGES) 
	            .contentType(MediaType.MULTIPART_FORM_DATA)
	            .body(BodyInserters.fromMultipartData(builder.build()))
	            .retrieve()
	            .bodyToMono(String.class)
	            .block();
	    return ResponseEntity.ok(response);
	}


	@GetMapping("/getplace/{placeId}")
	public ResponseEntity<PlaceResponseDTO> getPlace(@PathVariable Long placeId)
	{
		System.out.println("AdminPlaceController.getPlace()");
		PlaceResponseDTO dto=webClientBuilder.build()
		.get()
		.uri("lb://"+PLACE_SERVICE_NAME+PATH_TO_GET_PLACE,placeId)
		.retrieve()
		.bodyToMono(PlaceResponseDTO.class)
		.block();
		
		return ResponseEntity.ok(dto);
	}
	
	@PutMapping("/updateplace/{placeId}")
	public ResponseEntity<String> updatePlace(@PathVariable Long placeId,@RequestBody PlaceDTO dto)
	{
		System.out.println("AdminPlaceController.updatePlace()");
		String result=webClientBuilder.build()
		.put()
		.uri("lb://"+PLACE_SERVICE_NAME+PATH_TO_UPDATE_PLACE,placeId)
		.bodyValue(dto)
		.retrieve()
		.bodyToMono(String.class)
		.block();
		
		return ResponseEntity.ok(result);
	}
	
	@DeleteMapping("/deleteplace/{placeId}")
	public ResponseEntity<String> deletePlace(@PathVariable Long placeId)
	{
		System.out.println("AdminPlaceController.deletePlace()");
		String result=webClientBuilder
				.build()
				.delete()
				.uri("lb://"+PLACE_SERVICE_NAME+PATH_TO_DELETE_PLACE,placeId)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		return ResponseEntity.ok(result);
	}
    
	@GetMapping("/getall/category")
	public ResponseEntity<List<PlaceCategoryDTO>> getAllPlacesIdsByCategory()
	{
		System.out.println("AdminPlaceController.getAllPlacesIdsByCategory()");
		List<PlaceCategoryDTO> categories= webClientBuilder
				.build()
				.get()
				.uri("lb://"+PLACE_SERVICE_NAME+PATH_TO_GET_PLACE_IDS_AND_CATEGORIES)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<PlaceCategoryDTO>>() {})
				.block();
		return new ResponseEntity<List<PlaceCategoryDTO>>(categories,HttpStatus.OK);
	}
	
}
