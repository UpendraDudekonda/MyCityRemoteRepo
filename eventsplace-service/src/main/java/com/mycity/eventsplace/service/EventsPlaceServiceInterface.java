package com.mycity.eventsplace.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.shared.eventsdto.EventCardDto;
import com.mycity.shared.eventsdto.EventsDTO;


public interface EventsPlaceServiceInterface {

	    List<EventCardDto> getAllEventCards();
//	    EventDetailDTO getEventDetails(String eventName);
	
		String addEvent(EventsDTO dto, MultipartFile mainImageFile, List<MultipartFile> galleryImages);
	
}
