package com.mycity.place.serviceImpl;

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
    private static final String IMAGE_FETCH_PATH = "/media/images/{placeId}";
    private static final String IMAGE_DELETING_PATH="/media/images/delete/{placeId}"; 
    private static final String PATH_TO_ADD_IMAGES_TO_GALLERY="/media/gallery/upload";
    private static final String PATH_TO_GET_IMAGES="/media/gallery/getimages/{districtName}";
    private static final String IMAGE_UPLOAD_PATH_FOR_PLACES = "/media/upload/places";
    private static final String IMAGE_UPLOAD_PATH_FOR_CUISINES = "/media/upload/cuisines";
    
    // Method to upload an image for a place
    public void uploadImageForPlace(MultipartFile image, long placeId, String placeName, String placeCategory, String imageName) {
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
            bodyBuilder.part("category", placeCategory);
            bodyBuilder.part("imageName", imageName);

            // Use Load Balancer routing to send the request to the IMAGE-SERVICE for image upload
            webClientBuilder.build()
                    .post()
                    .uri("lb://" + IMAGE_SERVICE + IMAGE_UPLOAD_PATH_FOR_PLACES) // Load balanced URI to upload the image
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
    
    //To Upload IMAGES to Gallery Section
    public String uploadImageToGallery(MultipartFile file, Long userId, Long placeId, UserGalleryDTO dto) {
        try {
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

            // Add image as part
            bodyBuilder.part("image", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            }).contentType(MediaType.parseMediaType(file.getContentType()));

            // Add metadata
            bodyBuilder.part("userId", userId.toString());
            bodyBuilder.part("placeId", placeId.toString());
            bodyBuilder.part("placeName", dto.getPlaceName());
            bodyBuilder.part("city", dto.getCity());
            bodyBuilder.part("district", dto.getDistrict());
            bodyBuilder.part("state", dto.getState());

            // Send multipart request
            return webClientBuilder.build()
                    .post()
                    .uri("lb://" + IMAGE_SERVICE + PATH_TO_ADD_IMAGES_TO_GALLERY)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(bodyBuilder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error uploading image: " + e.getMessage();
        }
    }
    
    //gives list of image urls from media-service based on districtName
    public List<String> getImagesByDistrict(String districtName) {
        try {
            return webClientBuilder
                    .build()
                    .get()
                    .uri("lb://"+IMAGE_SERVICE+PATH_TO_GET_IMAGES,districtName)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                    .block();
        } catch (Exception e) {
            System.err.println("Failed to fetch images for district: " + districtName);
            e.printStackTrace();
            return List.of();
        }

    }
    
    public String uploadCuisineImage(MultipartFile image, String cuisineName, long placeId, @NonNull String placeName, String placeCategory) {
    	System.out.println("WebClientMediaService.uploadCuisineImage()");
        try {
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder.part("image", new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            }).contentType(MediaType.parseMediaType(image.getContentType()));
 
            
            bodyBuilder.part("placeId", placeId);
            bodyBuilder.part("placeName", placeName);
            bodyBuilder.part("category", placeCategory);
            bodyBuilder.part("entityName", cuisineName);
 
            return webClientBuilder.build()
                    .post()
                    .uri("lb://" + IMAGE_SERVICE + IMAGE_UPLOAD_PATH_FOR_CUISINES)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(bodyBuilder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(response -> System.out.println("Cuisine image uploaded: " + response))
                    .doOnError(error -> System.err.println("Cuisine image upload failed: " + error.getMessage()))
                    .block(); // Block to return the uploaded image URL or message
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
