package com.mycity.user.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;

import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.placedto.UserGalleryDTO;


@Service
public class WebClientMediaService 
{    
    @Autowired
    private WebClient.Builder webClientBuilder;

    // Define constants for image service URL and paths
    private static final String IMAGE_SERVICE = "MEDIA-SERVICE";
    private static final String PATH_TO_ADD_IMAGES_TO_GALLERY="/media/gallery/upload";
    private static final String PATH_TO_GET_IMAGES="/media/gallery/getimages/{districtName}";
    private static final String PATH_TO_DELETE_IMAGE="/media/gallery/deleteimage/{imageId}";
    
    
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
    
    public String deleteImage(Long imageId) 
    {
        try 
        {
            return webClientBuilder
                    .build()
                    .delete()
                    .uri("lb://" + IMAGE_SERVICE + PATH_TO_DELETE_IMAGE, imageId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }
        catch (Exception e) 
        {
            throw new RuntimeException("Failed to delete image");
        }
    }

}
