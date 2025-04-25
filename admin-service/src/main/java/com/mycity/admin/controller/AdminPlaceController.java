package com.mycity.admin.controller;

import java.util.List; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.admin.service.AdminServiceInterface;
import com.mycity.shared.admindto.AdminPlaceResponseDTO;

@RestController
@RequestMapping("/admin")
public class AdminPlaceController 
{
	
	@Autowired
	private AdminServiceInterface service;
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
		//use service
		List<AdminPlaceResponseDTO> response=service.getAllPlaceDetails();
		return new ResponseEntity<List<AdminPlaceResponseDTO>>(response,HttpStatus.OK);
	}

}
