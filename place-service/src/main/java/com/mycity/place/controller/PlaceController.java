package com.mycity.place.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.place.entity.Place;
import com.mycity.place.exception.GlobalExceptionHandler;
import com.mycity.place.service.PlaceServiceInterface;
import com.mycity.shared.placedto.PlaceCategoryDTO;
import com.mycity.shared.placedto.PlaceDTO;
import com.mycity.shared.placedto.PlaceResponseDTO;

@RestController
@RequestMapping("/place")
public class PlaceController {

    private final GlobalExceptionHandler globalExceptionHandler;

	@Autowired
	private PlaceServiceInterface placeService;

    PlaceController(GlobalExceptionHandler globalExceptionHandler) {
        this.globalExceptionHandler = globalExceptionHandler;
    }

	@PostMapping(value = "/add-place", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addPlaceDetails(@ModelAttribute PlaceDTO placeDto,
			@RequestPart("images") List<MultipartFile> images) {
		System.out.println("PlaceController.addPlaceDetails()");
		System.out.println(placeDto.getCategoryName());
 
		try {
 
			// Use the service to add the place details and save the images
			String msg = placeService.addPlace(placeDto, images);
			return new ResponseEntity<>(msg, HttpStatus.CREATED);
 
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error adding place with images", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}




	// Create a place using PlaceDTO
	@PostMapping("/newplace/add")
	public ResponseEntity<String> addPlaceDetails(@RequestBody PlaceDTO dto) {
		String msg = placeService.addPlace(dto);
		return new ResponseEntity<>(msg, HttpStatus.CREATED);
	}

	// Get place by ID (Place entity)
	@GetMapping("/get/{placeId}")
	public ResponseEntity<PlaceResponseDTO> getPlaceDetails(@PathVariable Long placeId) {
		PlaceResponseDTO place = placeService.getPlace(placeId);
		return new ResponseEntity<>(place, HttpStatus.OK);
	}

	// Update place using PlaceDTO
	@PutMapping("/update/{placeId}")
	public ResponseEntity<String> updatePlaceDetails(@PathVariable Long placeId, @RequestBody PlaceDTO dto) {
		String msg = placeService.updatePlace(placeId, dto);
		return new ResponseEntity<>(msg, HttpStatus.OK);
	}

	// Delete place
	@DeleteMapping("/delete/{placeId}")
	public ResponseEntity<String> deletePlaceDetails(@PathVariable Long placeId) {
		String msg = placeService.deletePlace(placeId);
		return new ResponseEntity<>(msg, HttpStatus.OK);
	}

//    @PostMapping("/save")
//    public ResponseEntity<Place> createPlace(@RequestBody Place place) {
//        Place savedPlace = placeService.savePlace(place);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlace);
//    }

	@GetMapping("/allplaces")
	public ResponseEntity<List<PlaceResponseDTO>> getAllPlaces() {
		return ResponseEntity.ok(placeService.getAllPlaces());
	}

	@GetMapping("/placeby/{id}")
	public ResponseEntity<Place> getPlaceById(@PathVariable Long id) {
		Place place = placeService.getPlaceById(id);
		return place != null ? ResponseEntity.ok(place) : ResponseEntity.notFound().build();
	}

	@GetMapping("/placeby/categories")
	public ResponseEntity<List<PlaceCategoryDTO>> getAllDistinctCategories() {
		List<PlaceCategoryDTO> categories = placeService.getAllDistinctCategories();
		return ResponseEntity.ok(categories);
	}
	
	@GetMapping("/getplaceid/{placeName}")
	public ResponseEntity<Long> getPlaceIdByName(@PathVariable String placeName) {
	    Long placeId = placeService.getPlaceIdByName(placeName);
	    if (placeId != null) {
	        return new ResponseEntity<>(placeId, HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}


}
