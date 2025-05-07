package com.mycity.media.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;


import com.mycity.shared.mediadto.EventSubImagesDTO;
import com.mycity.shared.mediadto.ImageDTO;

public interface EventImageService {

	

	void uploadImages(List<MultipartFile> files, List <String> names, Long eventId, String eventName);

//	EventSubImagesDTO fetchImages(Long eventId);

	void deleteAssociatedImages(Long eventId);

	String updateEventImages(Long eventId, String eventName, List<MultipartFile> files, List<String> names);
}
