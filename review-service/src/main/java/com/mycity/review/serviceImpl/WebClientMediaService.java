package com.mycity.review.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WebClientMediaService 
{ 
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    private static final String IMAGE_SERVICE = "MEDIA-SERVICE";
    private static final String IMAGE_ADDING_PATH="/media/review/upload";
    private static final String IMAGE_DELETING_PATH="/media/review/delete/{reviewId}";
    private static final String IMAGE_FETCH_PATH = "/media/review/image/place/{placeId}";

    
    // Method to upload an image for review
    public void uploadImageForReview(MultipartFile image,Long reviewId,Long placeId,String placeName,Long userId,String userName) {
        try {
            // Build multipart body for the image upload request
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

            // Add the image data to the request body
            bodyBuilder.part("image", new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename(); // Use the original file name
                }
            }).contentType(MediaType.parseMediaType(image.getContentType()));

            // Add place-related metadata to the request
            bodyBuilder.part("reviewId",reviewId);
            bodyBuilder.part("placeId", placeId);
            bodyBuilder.part("placeName", placeName);
            bodyBuilder.part("userId", userId);
            bodyBuilder.part("userName",userName);

            // Use Load Balancer routing to send the request to the IMAGE-SERVICE for image upload
            webClientBuilder.build()
                    .post()
                    .uri("lb://" + IMAGE_SERVICE + IMAGE_ADDING_PATH) // Load balanced URI to upload the image
                    .contentType(MediaType.MULTIPART_FORM_DATA) // Set the content type as multipart/form-data
                    .bodyValue(bodyBuilder.build()) // Attach the body to the request
                    .retrieve() // Execute the request
                    .bodyToMono(String.class) // Expect a string response (could be a success message or image ID)
                    .subscribe(response -> System.out.println("Image uploaded: " + response),
                            error -> System.err.println("Upload failed: " + error.getMessage())); // Handle success or failure
        } catch (Exception e) {
            e.printStackTrace(); // Handle any exceptions that occur during the upload process
        }
    }
    
    //media to delete Images
    public String deleteImagesForReviews(Long reviewId)
    {
    	//use webClient to invoke media-service
    	String result=webClientBuilder
    			.build()
    			.delete()
    			.uri("lb://"+IMAGE_SERVICE+IMAGE_DELETING_PATH,reviewId)
    			.retrieve()
    			.bodyToMono(String.class)
    			.block();
    	
    	return result;
    }
    
    public String getImageUrlByPlaceId(Long placeId) {
        try {
            return webClientBuilder
                    .build()
                    .get()
                    .uri("lb://" + IMAGE_SERVICE + IMAGE_FETCH_PATH, placeId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            System.err.println("Failed to fetch image for placeId " + placeId + ": " + e.getMessage());
            return null;
        }
    }


}
