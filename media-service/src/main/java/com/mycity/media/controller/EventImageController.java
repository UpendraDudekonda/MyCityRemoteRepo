package com.mycity.media.controller;


import java.util.List;

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

import com.mycity.media.service.EventImageService;
import com.mycity.media.service.ImageService;
import com.mycity.shared.mediadto.EventMainImageDTO;
import com.mycity.shared.mediadto.EventSubImagesDTO;
import com.mycity.shared.mediadto.ImageDTO;

import jakarta.ws.rs.core.MediaType;

@RestController
@RequestMapping("/upload")
public class EventImageController {
     
	@Autowired
	private EventImageService imageService;
	
	@PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA)
	public ResponseEntity<String> uploadImage(
						    @RequestPart("file") MultipartFile file,  // <-- match WebClient
				    @RequestParam Long eventId,
				    @RequestParam String eventName) {		
		imageService.uploadImage(file,eventId,eventName);
	    return ResponseEntity.ok("Image uploaded successfully");
	}
	
	
	@PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA)
	public ResponseEntity<String> uploadImages(
	        @RequestPart("files") List<MultipartFile> files, // Accepting multiple files
	        @RequestParam Long eventId,
	        @RequestParam String eventName
	) {
	    imageService.uploadImages(files, eventId, eventName); // Pass files to the service

	    return ResponseEntity.ok("Images uploaded successfully");
	}

//	@GetMapping("/fetch/{id}")
//	public ResponseEntity<EventMainImageDTO> getImage(@PathVariable Long id) {
//	    EventMainImageDTO imageDTO = imageService.fetchImage(id);
//	    return ResponseEntity.ok(imageDTO);
//	}
//	
//	@GetMapping("/fetch/{id}")
//	public ResponseEntity<EventSubImagesDTO> getImages(@PathVariable Long id) {
//	    EventSubImagesDTO imageDTO = imageService.fetchImages(id);
//	    return ResponseEntity.ok(imageDTO);
//	}
}
