package com.mycity.eventsplace.service;

import java.util.List;
import java.util.Map;


import org.springframework.web.multipart.MultipartFile;

import com.mycity.shared.eventsdto.EventsDTO;
import com.mycity.shared.placedto.AboutPlaceEventDTO;


public interface EventsPlaceServiceInterface {


		String addEvent(EventsDTO dto, List<MultipartFile> galleryImages, List<String> imageNames);


		String updateEvent(Long eventId, EventsDTO eventDTO, List<MultipartFile> galleryImages,
				List<String> imageNames);


		String deleteEvent(Long eventId);
 
	
		
		 Map<String,Object> createEventCartSection();

		 
		Map<String, Object> createEventDetailsSection(Long eventId);


		
}
