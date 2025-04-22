package com.mycity.media.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mycity.shared.mediadto.EventMainImageDTO;
import com.mycity.shared.mediadto.EventSubImagesDTO;
import com.mycity.shared.mediadto.ImageDTO;

public interface EventImageService {

	void uploadImage(MultipartFile file, Long eventId, String eventName);

	EventMainImageDTO fetchImage(Long eventId);

	void uploadImages(List<MultipartFile> files, Long eventId, String eventName);

	EventSubImagesDTO fetchImages(Long eventId);
}
