package com.mycity.place.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.place.entity.Place;
import com.mycity.place.service.PlaceServiceInterface;
import com.mycity.shared.placedto.PlaceDTO;

@RestController
@RequestMapping("auth/place-api")
public class PlaceController 
{
	@Autowired
    private PlaceServiceInterface service;
	
	@PostMapping("/addplace")
	public ResponseEntity<String> addPlaceDetails(@RequestBody PlaceDTO dto)
	{
		System.out.println("PlaceController.addPlaceDetails()");
		//use service
		String msg=service.addPlace(dto);
		return new ResponseEntity<String>(msg,HttpStatus.CREATED);
	}
	
	@GetMapping("/getplace/{placeId}")
	public ResponseEntity<Place> getPlaceDetails(@PathVariable Long PlaceId)
	{
		//use service
		Place place=service.getPlace(PlaceId);
		return new ResponseEntity<Place>(place,HttpStatus.FOUND);	
	}
	
	@PutMapping("/updateplace/{placeId}")
	public ResponseEntity<String> updatePlaceDetails(@PathVariable Long placeId,@RequestBody PlaceDTO dto)
	{
		//use service
		String msg=service.updatePlace(placeId, dto);
		return new ResponseEntity<String>(msg,HttpStatus.OK);
	}
	
	@DeleteMapping("/deleteplace/{placeId}")
	public ResponseEntity<String> deletePlaceDetails(@PathVariable Long placeId)
	{
		//use service
		String msg=service.deletePlace(placeId);
		return new ResponseEntity<String>(msg,HttpStatus.OK);
	}
}
