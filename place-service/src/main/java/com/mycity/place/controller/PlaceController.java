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
import com.mycity.place.service.PlaceServiceInterface;
import com.mycity.place.serviceImpl.WebClientMediaService;
import com.mycity.shared.placedto.PlaceDTO;


@RestController
@RequestMapping("/place")
public class PlaceController {

	@Autowired
	private PlaceServiceInterface service;

	public PlaceController(PlaceServiceInterface service) {
		this.service = service;
	}

	@PostMapping(value = "/add-place", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addPlaceDetails(@ModelAttribute PlaceDTO placeDto,
			@RequestPart("images") List<MultipartFile> images) {
		System.out.println("PlaceController.addPlaceDetails()");

		try {
			
			// Use the service to add the place details and save the images
			String msg = service.addPlace(placeDto, images);
			return new ResponseEntity<>(msg, HttpStatus.CREATED);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error adding place with images", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//
//	@PostMapping("/addplace")
//	public ResponseEntity<String> addPlaceDetails(@RequestBody PlaceDTO dto) {
//		System.out.println("PlaceController.addPlaceDetails()");
//		// use service
//		String msg = service.addPlace(dto);
//		return new ResponseEntity<String>(msg, HttpStatus.CREATED);
//	}

	@GetMapping("/getplace/{placeId}")
	public ResponseEntity<Place> getPlaceDetails(@PathVariable Long PlaceId) {
		// use service
		Place place = service.getPlace(PlaceId);
		return new ResponseEntity<Place>(place, HttpStatus.FOUND);
	}

	@PutMapping("/updateplace/{placeId}")
	public ResponseEntity<String> updatePlaceDetails(@PathVariable Long placeId, @RequestBody PlaceDTO dto) {
		// use service
		String msg = service.updatePlace(placeId, dto);
		return new ResponseEntity<String>(msg, HttpStatus.OK);
	}

	@DeleteMapping("/deleteplace/{placeId}")
	public ResponseEntity<String> deletePlaceDetails(@PathVariable Long placeId) {
		// use service
		String msg = service.deletePlace(placeId);
		return new ResponseEntity<String>(msg, HttpStatus.OK);
	}
}
