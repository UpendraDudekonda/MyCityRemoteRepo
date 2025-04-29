package com.mycity.eventsplace.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Component; 
import com.mycity.shared.mediadto.EventSubImagesDTO;

@Component
public class MediaServiceConfig {

	@Autowired
	  private  WebClient.Builder webClientBuilder;

   
    private static final String MEDIA_BASE_URL = "http://localhost:8094";
    
    public void uploadGalleryImages(
            List<MultipartFile> galleryImages,
            List<String> imageNames,
            EventSubImagesDTO dto) {

        WebClient webClient = webClientBuilder.build();

        for (int i = 0; i < galleryImages.size(); i++) {
            MultipartFile file = galleryImages.get(i);
            String customName = imageNames.size() > i ? imageNames.get(i) : file.getOriginalFilename();

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("files", file.getResource());
            body.add("names", customName);
            body.add("eventId", dto.getEventId());
            body.add("eventName", dto.getEventName());

            String response = webClient
                    .post()
                    .uri(MEDIA_BASE_URL + "/media/upload/images")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Uploaded: " + customName + " => " + response);
        }
    }
    
    
    public void updateEventImagesInMediaService(Long eventId, String eventName, List<MultipartFile> galleryImages, List<String> imageNames) {
        try {
        	WebClient webClient = webClientBuilder.build();

        	for (int i = 0; i < galleryImages.size(); i++) {
        	    MultipartFile file = galleryImages.get(i);
        	    String customName = imageNames.size() > i ? imageNames.get(i) : file.getOriginalFilename();

        	    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        	    body.add("files", file.getResource());
        	    body.add("names", customName);
        	    body.add("eventId", eventId);
        	    body.add("eventName",eventName);

        	    String response = webClient
        	            .put()
        	            .uri(MEDIA_BASE_URL + "/media/update/images/{eventId}" ,eventId)
        	            .contentType(MediaType.MULTIPART_FORM_DATA)
        	            .body(BodyInserters.fromMultipartData(body))
        	            .retrieve()
        	            .bodyToMono(String.class)
        	            .block();

        	    System.out.println("Uploaded: " + customName + " => " + response);
        	}
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
        String mediaResponse = webClient
                .post()
                .uri(MEDIA_BASE_URL + "/media/delete/images/{eventId}" ,eventId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("eventName", eventName))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("Deleted media r esponse: " + mediaResponse);
    } catch (Exception e) {
        System.err.println("Failed to delete media images: " + e.getMessage());
    }
    }


}
