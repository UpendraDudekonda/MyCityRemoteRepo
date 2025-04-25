package com.mycity.place.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mycity.place.service.PlaceDiscoveriesInterface;
import com.mycity.shared.placedto.PlaceDTO;
import com.mycity.shared.placedto.PlaceDiscoveriesDTO;

@RestController
@RequestMapping("/place/discoveries")
public class PlaceDiscoveriesController 
{
	@Autowired
    private PlaceDiscoveriesInterface service;
	
	@PostMapping("/add")
	public ResponseEntity<String> addPlaceToDiscovery(@RequestBody PlaceDiscoveriesDTO dto)
	{
	    //use service
		String msg=service.addPlaceToDiscoveries(dto);
		return new ResponseEntity<String>(msg,HttpStatus.CREATED);
	} 
	
	@GetMapping("/getall")
	public ResponseEntity<List<PlaceDiscoveriesDTO>> getAllPlacesFromDisoveries()
	{
		//use service
		List<PlaceDiscoveriesDTO> discoveries=service.getAllTopDisoveries();
		return new ResponseEntity<List<PlaceDiscoveriesDTO>>(discoveries,HttpStatus.OK);
	}
	
	@GetMapping("/getplace/{placeName}")
	public ResponseEntity<PlaceDTO> getPlaceDetails(@PathVariable String placeName)
	{
		//use service
		PlaceDTO place=service.getPlaceDetailsByName(placeName);
		return new ResponseEntity<PlaceDTO>(place,HttpStatus.FOUND);
		
	}
}
