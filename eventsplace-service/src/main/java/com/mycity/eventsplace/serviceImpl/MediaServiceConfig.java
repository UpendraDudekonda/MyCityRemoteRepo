package com.mycity.eventsplace.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.stereotype.Component; 
import com.mycity.shared.mediadto.EventSubImagesDTO;

@Component
public class MediaServiceConfig {

	@Autowired
	  private  WebClient.Builder webClientBuilder;

	
   
    private static final String MEDIA_BASE_URL = "MEDIA-SERVICE";
    private static final String MEDIA_ADD_IMAGES = "/media/upload/images";
    private static final String MEDIA_UPDATE_IMAGES="/media/update/images/";
    private static final String MEDIA_DELETE_IMAGES= "/media/delete/images/";
    private static final String MEDIA_FETCH_IMAGES="/media/fetch/images/";
    
    public void uploadGalleryImages(
            List<MultipartFile> galleryImages,
            List<String> imageNames,
            EventSubImagesDTO dto) {

        WebClient webClient = webClientBuilder.build();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (int i = 0; i < galleryImages.size(); i++) {
            MultipartFile file = galleryImages.get(i);
            String customName = imageNames.size() > i ? imageNames.get(i) : file.getOriginalFilename();

           
            body.add("files", file.getResource());
            body.add("names", customName);
        }
            body.add("eventId", dto.getEventId());
            body.add("eventName", dto.getEventName());

            String response = webClient
                    .post()
                    .uri("lb://" +MEDIA_BASE_URL + MEDIA_ADD_IMAGES)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("added images for the event: " + dto.getEventName() + " => " + response);
        
    }
    
    
    public void updateEventImagesInMediaService(Long eventId, String eventName, List<MultipartFile> galleryImages, List<String> imageNames) {
        try {
        	WebClient webClient = webClientBuilder.build();
        	 MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        	for (int i = 0; i < galleryImages.size(); i++) {
        	    MultipartFile file = galleryImages.get(i);
        	    String customName = imageNames.size() > i ? imageNames.get(i) : file.getOriginalFilename();

        	   
        	    body.add("files", file.getResource());
        	    body.add("names", customName);
        	}
        	    body.add("eventId", eventId);
        	    body.add("eventName",eventName);
        	
        	    String response = webClient
        	            .put()
        	            .uri("lb://"+MEDIA_BASE_URL + MEDIA_UPDATE_IMAGES +eventId)
        	            .contentType(MediaType.MULTIPART_FORM_DATA)
        	            .body(BodyInserters.fromMultipartData(body))
        	            .retrieve()
        	            .bodyToMono(String.class)
        	            .block();

        	    System.out.println("Uploaded new images for the event: " + eventName + " => " + response);
        	
        }
catch (Exception e) {
            System.err.println("Error while updating images in media service: " + e.getMessage());
        }
    }
    
    
    public void deleteEventImages(Long eventId,String eventName) {
    try {
        MultiValueMap<String, Object> deleteRequest = new LinkedMultiValueMap<>();
        deleteRequest.add("eventId", eventId);
       

        WebClient webClient = webClientBuilder.build();
        String mediaResponse = ((RequestBodySpec) webClient
                .delete()
                .uri("lb://"+MEDIA_BASE_URL + MEDIA_DELETE_IMAGES +eventId))
                .body(BodyInserters.fromFormData("eventName", eventName))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("Deleted media response: " + mediaResponse);
    } catch (Exception e) {
        System.err.println("Failed to delete media images: " + e.getMessage());
    }
    }

    
    public List<String> getImageUrlsForEvent(Long eventId) {
        List<String> images = new ArrayList<>();

        try {
            WebClient webClient = webClientBuilder.build();

            images = webClient
            		 .get()
            		    .uri("lb://"+MEDIA_BASE_URL +MEDIA_FETCH_IMAGES + eventId)
            		    .retrieve()
            		    .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
            		    .block();
        } catch (Exception e) {
            System.err.println("Failed to fetch event images by name: " + e.getMessage());
        }

        return images;
    }


	
}
