package com.mycity.place.serviceImpl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mycity.place.exception.MediaServiceUnavailableException;
import com.mycity.shared.mediadto.AboutPlaceImageDTO;
import com.mycity.shared.placedto.UserGalleryDTO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class WebClientMediaService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final String IMAGE_SERVICE = "MEDIA-SERVICE";
    private static final String IMAGE_FETCH_PATH = "/media/images/{placeId}";
    private static final String IMAGE_DELETING_PATH = "/media/images/delete/{placeId}";
    private static final String PATH_TO_ADD_IMAGES_TO_GALLERY = "/media/gallery/upload";
    private static final String PATH_TO_GET_IMAGES = "/media/gallery/getimages/{districtName}";
    private static final String IMAGE_UPLOAD_PATH_FOR_PLACES = "/media/upload/places";
    private static final String IMAGE_UPLOAD_PATH_FOR_CUISINES = "/media/upload/cuisines";
    private static final String IMAGE_FETCH_PATH_FOR_TOP_10_DESCOVIERS = "/media/image-byplacename/{placeName}";

    public void uploadImageForPlace(MultipartFile image, long placeId, String placeName, String placeCategory, String imageName) {
        try {
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder.part("image", new ByteArrayResource(image.getBytes()) {
                @Override public String getFilename() { return image.getOriginalFilename(); }
            }).contentType(MediaType.parseMediaType(image.getContentType()));
            bodyBuilder.part("placeId", placeId);
            bodyBuilder.part("placeName", placeName);
            bodyBuilder.part("category", placeCategory);
            bodyBuilder.part("imageName", imageName);

            webClientBuilder.build()
                .post()
                .uri("lb://" + IMAGE_SERVICE + IMAGE_UPLOAD_PATH_FOR_PLACES)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(bodyBuilder.build())
                .retrieve()
                .onStatus(HttpStatus.SERVICE_UNAVAILABLE::equals, res -> {
                    log.error("Media Service unavailable during image upload for placeId {}", placeId);
                    return Mono.error(new MediaServiceUnavailableException("Media service is down"));
                })
                .bodyToMono(String.class)
                .subscribe(
                    response -> log.info("Image uploaded: {}", response),
                    error -> log.error("Image upload failed: {}", error.getMessage())
                );

        } catch (Exception e) {
            log.error("Exception while uploading image: ", e);
        }
    }

    public CompletableFuture<List<AboutPlaceImageDTO>> getImagesForPlace(@NonNull Long placeId) {
        return webClientBuilder.build()
            .get()
            .uri("lb://" + IMAGE_SERVICE + IMAGE_FETCH_PATH, placeId)
            .retrieve()
            .onStatus(HttpStatus.SERVICE_UNAVAILABLE::equals, res -> {
                log.error("503 from Media Service when fetching images for placeId {}", placeId);
                return Mono.error(new MediaServiceUnavailableException("Media service unavailable"));
            })
            .bodyToFlux(AboutPlaceImageDTO.class)
            .collectList()
            .onErrorResume(ex -> {
                log.warn("Error fetching images for placeId {}: {}", placeId, ex.getMessage());
                // Return a fallback DTO with the error message
                AboutPlaceImageDTO fallback = new AboutPlaceImageDTO();
                fallback.setImageName("Error");
                fallback.setImageUrl("Media service is currently unavailable");
                return Mono.just(List.of(fallback));
            })
            .toFuture();
    }


    public String deleteImages(Long placeId) {
        try {
            return webClientBuilder.build()
                .delete()
                .uri("lb://" + IMAGE_SERVICE + IMAGE_DELETING_PATH, placeId)
                .retrieve()
                .onStatus(HttpStatus.SERVICE_UNAVAILABLE::equals, res -> {
                    log.error("503 error while deleting images for placeId {}", placeId);
                    return Mono.error(new MediaServiceUnavailableException("Media service unavailable"));
                })
                .bodyToMono(String.class)
                .block();
        } catch (Exception e) {
            log.error("Exception while deleting images: ", e);
            return "Error deleting images: " + e.getMessage();
        }
    }

    public String uploadImageToGallery(MultipartFile file, Long userId, Long placeId, UserGalleryDTO dto) {
        try {
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder.part("image", new ByteArrayResource(file.getBytes()) {
                @Override public String getFilename() { return file.getOriginalFilename(); }
            }).contentType(MediaType.parseMediaType(file.getContentType()));
            bodyBuilder.part("userId", userId.toString());
            bodyBuilder.part("placeId", placeId.toString());
            bodyBuilder.part("placeName", dto.getPlaceName());
            bodyBuilder.part("city", dto.getCity());
            bodyBuilder.part("district", dto.getDistrict());
            bodyBuilder.part("state", dto.getState());

            return webClientBuilder.build()
                .post()
                .uri("lb://" + IMAGE_SERVICE + PATH_TO_ADD_IMAGES_TO_GALLERY)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(bodyBuilder.build())
                .retrieve()
                .onStatus(HttpStatus.SERVICE_UNAVAILABLE::equals, res -> {
                    log.error("503 error while uploading gallery image for user {}", userId);
                    return Mono.error(new MediaServiceUnavailableException("Media service unavailable"));
                })
                .bodyToMono(String.class)
                .block();

        } catch (Exception e) {
            log.error("Exception during gallery image upload: ", e);
            return "Error uploading image: " + e.getMessage();
        }
    }

    public List<String> getImagesByDistrict(String districtName) {
        try {
            return webClientBuilder.build()
                .get()
                .uri("lb://" + IMAGE_SERVICE + PATH_TO_GET_IMAGES, districtName)
                .retrieve()
                .onStatus(HttpStatus.SERVICE_UNAVAILABLE::equals, res -> {
                    log.error("503 error fetching images for district {}", districtName);
                    return Mono.error(new MediaServiceUnavailableException("Media service unavailable"));
                })
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();
        } catch (Exception e) {
            log.error("Error fetching district images: ", e);
            return List.of();
        }
    }

    public String uploadCuisineImage(MultipartFile image, String cuisineName, long placeId, @NonNull String placeName, String placeCategory) {
        try {
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder.part("image", new ByteArrayResource(image.getBytes()) {
                @Override public String getFilename() { return image.getOriginalFilename(); }
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
                .onStatus(HttpStatus.SERVICE_UNAVAILABLE::equals, res -> {
                    log.error("503 error uploading cuisine image for placeId {}", placeId);
                    return Mono.error(new MediaServiceUnavailableException("Media service unavailable"));
                })
                .bodyToMono(String.class)
                .block();

        } catch (Exception e) {
            log.error("Error uploading cuisine image: ", e);
            return null;
        }
    }

    public CompletableFuture<List<String>> getImageUrlsForPlace(Long placeId) {
        return webClientBuilder.build()
            .get()
            .uri("lb://" + IMAGE_SERVICE + IMAGE_FETCH_PATH, placeId)
            .retrieve()
            .onStatus(HttpStatus.SERVICE_UNAVAILABLE::equals, res -> {
                log.error("503 while fetching image URLs for placeId {}", placeId);
                return Mono.error(new MediaServiceUnavailableException("Media service unavailable"));
            })
            .bodyToFlux(String.class)
            .collectList()
            .onErrorResume(ex -> {
                log.warn("Failed to fetch image URLs for placeId {}: {}", placeId, ex.getMessage());
                return Mono.just(List.of());
            })
            .toFuture();
    }

    public List<String> getPhotoUrlsByPlaceId(long placeId) {
        try {
            return getImageUrlsForPlace(placeId).get();
        } catch (Exception e) {
            log.error("Error in getPhotoUrlsByPlaceId: ", e);
            return List.of();
        }
    }
    public CompletableFuture<List<AboutPlaceImageDTO>> getImagesForTop10Descoveries(@NonNull String placeName) {
        return webClientBuilder.build()
            .get()
            .uri("lb://" + IMAGE_SERVICE + IMAGE_FETCH_PATH_FOR_TOP_10_DESCOVIERS, placeName)
            .retrieve()
            .onStatus(HttpStatus.SERVICE_UNAVAILABLE::equals, res -> {
                log.error("503 from Media Service when fetching images for placeId {}", placeName);
                return Mono.error(new MediaServiceUnavailableException("Media service unavailable"));
            })
            .bodyToFlux(AboutPlaceImageDTO.class)
            .collectList()
            .onErrorResume(ex -> {
                log.warn("Error fetching images for placeId {}: {}", placeName, ex.getMessage());
                // Return a fallback DTO with the error message
                AboutPlaceImageDTO fallback = new AboutPlaceImageDTO();
                fallback.setImageName("Error");
                fallback.setImageUrl("Media service is currently unavailable");
                return Mono.just(List.of(fallback));
            })
            .toFuture();
    }
    
}
