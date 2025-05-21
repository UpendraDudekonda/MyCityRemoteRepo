package com.mycity.admin.serviceImpl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.mediadto.AboutPlaceImageDTO;
import com.mycity.shared.placedto.UserGalleryDTO;

@Service
public class WebClientMediaService 
{    
    @Autowired
    private WebClient.Builder webClientBuilder;

    // Define constants for image service URL and paths
    private static final String IMAGE_SERVICE = "MEDIA-SERVICE";
//    private static final String IMAGE_UPLOAD_PATH = "/media/upload";
    private static final String IMAGE_FETCH_PATH = "/media/images/{placeName}";
    private static final String IMAGE_DELETING_PATH="/media/images/delete/{placeId}"; 
    private static final String PATH_TO_GET_IMAGES="/media/gallery/getimages/{districtName}";
   

    public CompletableFuture<List<AboutPlaceImageDTO>> getImagesForPlace(@lombok.NonNull String string) {
        return webClientBuilder.build()
                .get()
                .uri("lb://" + IMAGE_SERVICE + IMAGE_FETCH_PATH, string)
                .retrieve()
                .bodyToFlux(AboutPlaceImageDTO.class) // Expecting list of DTOs instead of strings
                .collectList()
                .toFuture();
    }
    
    //Method to delete Images based on PlaceId
    public String deleteImages(Long placeId)
    {
        //use WebClientBuilder to router from Place-service to Media-Service
        return webClientBuilder
                .build()
                .delete()
                .uri("lb://"+IMAGE_SERVICE+IMAGE_DELETING_PATH,placeId)
                .retrieve()
                .bodyToMono(String.class)
                .block();  // blocking the response to wait for the delete operation
    }
    public CompletableFuture<List<String>> getImageUrlsForPlace(Long placeId) {
        // Use Load Balancer routing to send a request to the IMAGE-SERVICE for fetching image URLs by placeId
        return webClientBuilder.build()
                .get()
                .uri("lb://" + IMAGE_SERVICE + IMAGE_FETCH_PATH, placeId) // Load balanced URI to fetch image URLs
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
