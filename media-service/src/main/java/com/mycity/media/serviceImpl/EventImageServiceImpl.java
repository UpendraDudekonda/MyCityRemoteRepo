package com.mycity.media.serviceImpl;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.mycity.media.entity.EventSubImages;

import com.mycity.media.helper.CloudinaryHelper;

import com.mycity.media.repository.EventSubImagesRepository;

import com.mycity.media.service.EventImageService;

import com.mycity.shared.mediadto.EventSubImagesDTO;


@Service
public class EventImageServiceImpl implements EventImageService {
	
	@Autowired
    private CloudinaryHelper  cloudinaryHelper;

	
	@Autowired
	private EventSubImagesRepository eventSubImagesRepository;
	



//upload
@Override
	public void uploadImages(List<MultipartFile> files, List<String> names,Long eventId, String eventName) {
	    // Upload images to Cloudinary and get URLs
	    List<String> imageUrls = cloudinaryHelper.saveImages(files); // Now accepts a List<MultipartFile> 

	    // Create EventSubImages object
	    EventSubImages img = new EventSubImages();
	    img.setImageUrls(imageUrls);
	    img.setImageNames(names);// Set the list of image URLs
	    img.setEventId(eventId);
	    img.setEventName(eventName);
	  

	    // Save image info in DB
	    eventSubImagesRepository.save(img);
	}

	
//updating images
@Override
public String updateEventImages(Long eventId, String eventName, List<MultipartFile> files, List<String> names) {

    // Step 1: Delete existing images for this event
    List<EventSubImages> existingImages = eventSubImagesRepository.findAllByEventId(eventId);
    if (existingImages != null && !existingImages.isEmpty()) {
        eventSubImagesRepository.deleteAll(existingImages);
    }

    // Step 2: Save new images
  
        List<String> imageUrls = cloudinaryHelper.saveImages(files);

        EventSubImages newImage = new EventSubImages();
        newImage.setEventId(eventId);
        newImage.setEventName(eventName);
        newImage.setImageNames(names);
        newImage.setImageUrls(imageUrls);

        eventSubImagesRepository.save(newImage);
    

    return "Images updated successfully for event: " + eventName;
}


//delete images
@Override
public void deleteAssociatedImages(Long eventId) {
    List<EventSubImages> images = eventSubImagesRepository.findAllByEventId(eventId);

    // 3. Delete from database
    eventSubImagesRepository.deleteAll(images);
    System.out.println("Deleted all gallery images from DB for eventId: " + eventId);
}


@Override
public List getEventImages(Long eventId) {
	List<EventSubImages> images = eventSubImagesRepository.findImageUrlsByEventId(eventId);
	return images;
	
}


	

}
