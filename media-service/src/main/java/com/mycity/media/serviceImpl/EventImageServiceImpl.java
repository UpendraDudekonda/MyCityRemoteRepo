package com.mycity.media.serviceImpl;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.media.entity.EventMainImage;
import com.mycity.media.entity.EventSubImages;
import com.mycity.media.entity.Images;
import com.mycity.media.helper.CloudinaryHelper;
import com.mycity.media.repository.EventMainImageRepository;
import com.mycity.media.repository.EventSubImagesRepository;
import com.mycity.media.repository.ImageServiceRepository;
import com.mycity.media.service.EventImageService;
import com.mycity.shared.mediadto.EventMainImageDTO;
import com.mycity.shared.mediadto.EventSubImagesDTO;
import com.mycity.shared.mediadto.ImageDTO;

@Service
public class EventImageServiceImpl implements EventImageService {
	
	@Autowired
    private CloudinaryHelper  cloudinaryHelper;
	
	@Autowired
	private EventMainImageRepository eventMainImageRepository;
	
	@Autowired
	private EventSubImagesRepository eventSubImagesRepository;
	

	@Override
	public void uploadImage(MultipartFile file, Long eventId, String eventName) {
		 // Upload to Cloudinary + save info in DB
	    String imageUrl = cloudinaryHelper.saveImage(file); // your Cloudinary logic

	    EventMainImage img=new EventMainImage();
	    img.setImageUrl(imageUrl);
	    img.setEventId(eventId);
	    img.setEventName(eventName);
	

	   eventMainImageRepository.save(img);
		
	}

	@Override
	public EventMainImageDTO fetchImage(Long eventId) {
		EventMainImage image = eventMainImageRepository.findById(eventId)
	        .orElseThrow(() -> new RuntimeException("Image not found with ID: " + eventId));

	    return new EventMainImageDTO(
	        image.getImageId(),
	        image.getImageUrl(),
	        image.getEventName(),
	        image.getEventId()
	    );
	}

@Override
	public void uploadImages(List<MultipartFile> files, Long eventId, String eventName) {
	    // Upload images to Cloudinary and get URLs
	    List<String> imageUrls = cloudinaryHelper.saveImages(files); // Now accepts a List<MultipartFile> 

	    // Create EventSubImages object
	    EventSubImages img = new EventSubImages();
	    img.setImageUrls(imageUrls); // Set the list of image URLs
	    img.setEventId(eventId);
	    img.setEventName(eventName);

	    // Save image info in DB
	    eventSubImagesRepository.save(img);
	}

	
	
	public EventSubImagesDTO fetchImages(Long eventId) {
		EventSubImages image = eventSubImagesRepository.findById(eventId)
	        .orElseThrow(() -> new RuntimeException("Image not found with ID: " + eventId));

	    return new EventSubImagesDTO(
	        image.getImageId(),
	       image.getImageUrls(),
	        image.getEventName(),
	        image.getEventId()
	    );
	}

}
