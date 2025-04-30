package com.mycity.media.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.media.service.EventImageService;

import jakarta.ws.rs.core.MediaType;

@RestController
@RequestMapping("/media")
public class EventImageController {
     
	@Autowired
	private EventImageService imageService;

	
	@PostMapping(value = "/upload/images", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON)
	public ResponseEntity<String> uploadImages(
	        @RequestPart("files") List<MultipartFile> files,
	        @RequestParam("names") List<String> names,// Accepting multiple files
	        @RequestParam Long eventId,
	        @RequestParam String eventName
	) {
	    imageService.uploadImages(files,names, eventId, eventName); // Pass files to the service

	    return ResponseEntity.ok("Images uploaded successfully");
	}
	
	
	@PutMapping(value ="/update/images/{eventId}", consumes=MediaType.MULTIPART_FORM_DATA, produces=MediaType.APPLICATION_JSON)
		public ResponseEntity<?> updateEventImages(
				@PathVariable Long eventId,
				@RequestParam String eventName,
				@RequestPart("files") List<MultipartFile> files, 
				@RequestParam("names") List<String> names){
		String res=imageService.updateEventImages(eventId, eventName, files, names);
		return ResponseEntity.ok(res);
		
	}


	 @DeleteMapping("/delete/images/{eventId}")
	    public ResponseEntity<?> deleteEventImages(
	            @PathVariable Long eventId,
	            @RequestParam String eventName) {
	        
		 imageService.deleteAssociatedImages(eventId);
	       

	        return ResponseEntity.ok("Images for event " + eventName + " deleted.");
	    }

//	
//	@GetMapping("/fetch/{id}")
//	public ResponseEntity<EventSubImagesDTO> getImages(@PathVariable Long id) {
//	    EventSubImagesDTO imageDTO = imageService.fetchImages(id);
//	    return ResponseEntity.ok(imageDTO);
//	}
}
