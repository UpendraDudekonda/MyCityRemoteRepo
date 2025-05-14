package com.mycity.media.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.media.repository.CuisineImageRepository;
import com.mycity.media.service.CuisineImageService;

import jakarta.ws.rs.core.MediaType;

@RestController
@RequestMapping("/media/upload")
public class CuisineImageController
{
 
	@Autowired
	private CuisineImageService service;
	
	@PostMapping(value = "/cuisines", consumes = MediaType.MULTIPART_FORM_DATA)
	public ResponseEntity<String> uplaodCuisineImage(
			@RequestPart("image") MultipartFile file,
	        @RequestParam Long placeId,
	        @RequestParam String placeName,
	        @RequestParam String category,
	        @RequestParam String CuisineName)
	{
		System.out.println("===CuisineImageController.uplaodCuisineImage()===");
		     String result=service.uploadCuisineImages(file, placeId, placeName, category, CuisineName);
		    return ResponseEntity.ok(result);
	}
}
