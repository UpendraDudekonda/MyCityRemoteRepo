package com.mycity.media.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.media.service.ImageService;
import com.mycity.shared.mediadto.ImageDTO;

import jakarta.ws.rs.core.MediaType;

@RestController
@RequestMapping("/img")
public class ImageController {
     
	@Autowired
	private ImageService imageService;
	
	
	
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA)
	public ResponseEntity<String> uploadImage(
	        @RequestPart("image") MultipartFile file,
	        @RequestParam Long placeId,
	        @RequestParam String placeName,
	        @RequestParam String category) {
		
		imageService.uploadImage(file,placeId,placeName,category);

	    return ResponseEntity.ok("Image uploaded successfully");
	}

	@GetMapping("/fetch/{id}")
	public ResponseEntity<ImageDTO> getImage(@PathVariable Long id) {
	    ImageDTO imageDTO = imageService.fetchImage(id);
	    return ResponseEntity.ok(imageDTO);
	}
	
	@GetMapping("/cover-image")
	public ResponseEntity<String> getCoverImageForCategory(@RequestParam String category) {
	    // You can return the first image URL from that category for now
	    String imageUrl = imageService.getFirstImageUrlByCategory(category);
	    return ResponseEntity.ok(imageUrl);
	}
	
}
