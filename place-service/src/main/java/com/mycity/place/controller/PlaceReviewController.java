package com.mycity.place.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.place.service.PlaceReviewService;
import com.mycity.place.service.PlaceServiceInterface;

@RestController
@RequestMapping("/place")
public class PlaceReviewController {

	@Autowired
	private PlaceServiceInterface service;

	@GetMapping("/review/placeId/{placeName}")
	public ResponseEntity<Long> getPlaceIdByPlaceName(@PathVariable String placeName) {
		System.out.println("PlaceController.getPlaceIdByPlaceName()");
		Long id = service.getPlaceIdByName(placeName);
		System.out.println("id Value ::" + id);
		return new ResponseEntity<Long>(id, HttpStatus.OK);
	}

}
