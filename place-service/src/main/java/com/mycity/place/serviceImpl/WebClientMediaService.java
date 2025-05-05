package com.mycity.place.serviceImpl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WebClientMediaService {
    
    @Autowired
    private WebClient.Builder webClientBuilder;

    // Define constants for image service URL and paths
    private static final String MEDIA_SERVICE = "MEDIA-SERVICE";
    private static final String IMAGE_UPLOAD_PATH = "/media/upload";
    private static final String IMAGE_FETCH_PATH = "/media/images/{placeId}";
    

    // Method to upload an image for a place
    public void uploadImageForPlace(MultipartFile image, long placeId, String placeName, String Category, String imageName) {
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
            bodyBuilder.part("placeId", placeId);
            bodyBuilder.part("placeName", placeName);
            bodyBuilder.part("category", Category);
            bodyBuilder.part("imageName", imageName);

            // Use Load Balancer routing to send the request to the IMAGE-SERVICE for image upload
            webClientBuilder.build()
                    .post()
                    .uri("lb://" + MEDIA_SERVICE + IMAGE_UPLOAD_PATH) // Load balanced URI to upload the image
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

    // Method to fetch image URLs for a specific place from the IMAGE-SERVICE
    public CompletableFuture<List<String>> getImageUrlsForPlace(Long placeId) {
        // Use Load Balancer routing to send a request to the IMAGE-SERVICE for fetching image URLs by placeId
        return webClientBuilder.build()
                .get()
                .uri("lb://" + MEDIA_SERVICE + IMAGE_FETCH_PATH, placeId) // Load balanced URI to fetch image URLs
                .retrieve() // Execute the request
                .bodyToFlux(String.class) // Expect the response to be a list of image URLs (strings)
                .collectList() // Collect the URLs into a list
                .toFuture(); // Return the result as a CompletableFuture
    }
    
    
    public List<String> getPhotoUrlsByPlaceId(long placeId) {
        try {
            return getImageUrlsForPlace(placeId).get(); // Waits for the result
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Return empty list on error
        }
    }

	
}
