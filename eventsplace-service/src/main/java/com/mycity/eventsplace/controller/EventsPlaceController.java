package com.mycity.eventsplace.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.eventsplace.service.EventsPlaceServiceInterface;
import com.mycity.shared.eventsdto.EventsDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/event/internal")
@RequiredArgsConstructor
public class EventsPlaceController {

	@Autowired
    private final EventsPlaceServiceInterface eventsPlaceService;

  
	@PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addEvent(
			 @ModelAttribute EventsDTO eventDTO,
	    @RequestPart("mainImage") MultipartFile mainImage,
	    @RequestPart("galleryImages") List<MultipartFile> galleryImages
	) 
 {
    	 String response = eventsPlaceService.addEvent(eventDTO, mainImage, galleryImages);
        return ResponseEntity.ok(response);
    }

}
