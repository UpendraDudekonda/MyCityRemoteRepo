package com.mycity.media.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mycity.media.entity.EventSubImages;


public interface EventImageService {

	
	

	void uploadImages(List<MultipartFile> files, List <String> names, Long eventId, String eventName);


	void deleteAssociatedImages(Long eventId);

	String updateEventImages(Long eventId, String eventName, List<MultipartFile> files, List<String> names);

	List<EventSubImages> getEventImages(Long eventId);
}
