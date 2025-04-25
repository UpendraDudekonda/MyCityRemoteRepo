package com.mycity.place.controller;

import java.util.List;

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
@RequestMapping("/place")
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
	public ResponseEntity<PlaceDTO> getPlaceDetails(@PathVariable Long placeId)
	{
		//use service
		PlaceDTO place=service.getPlace(placeId);
		return new ResponseEntity<PlaceDTO>(place,HttpStatus.FOUND);	
	}
	
	@GetMapping("/getid/{placeName}") 
	public ResponseEntity<Long> getPlaceIdByPlaceName(@PathVariable String placeName)
	{
		System.out.println("PlaceController.getPlaceIdByPlaceName()");
		//use service
		Long id=service.getPlaceIdByName(placeName);
		System.out.println("id Value ::"+id);
		return new ResponseEntity<Long>(id,HttpStatus.OK);
		
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
	
	@GetMapping("/getall") //to get all places when working with admin
	public ResponseEntity<List<PlaceDTO>> getAllPlaces()
	{
		System.out.println("PlaceController.getAllPlaces()............");
		//use service
		List<PlaceDTO> places=service.getAllPlaces();
		return new ResponseEntity<List<PlaceDTO>>(places,HttpStatus.FOUND);
	}
}
