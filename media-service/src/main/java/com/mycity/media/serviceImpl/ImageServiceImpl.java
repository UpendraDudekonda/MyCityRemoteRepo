package com.mycity.media.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.media.entity.Images;
import com.mycity.media.helper.CloudinaryHelper;
import com.mycity.media.repository.ImageServiceRepository;
import com.mycity.media.service.ImageService;
import com.mycity.shared.mediadto.ImageDTO;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private CloudinaryHelper cloudinaryHelper;

    @Autowired
    private ImageServiceRepository imageServiceRepository;

    @Override
    public void uploadImage(MultipartFile file, Long placeId, String placeName, String category,String imageName) {
        String imageUrl = cloudinaryHelper.saveImage(file); // your Cloudinary logic

        Images img = new Images();
        img.setImageUrl(imageUrl);
        img.setPlaceId(placeId);
        img.setPlaceName(placeName);
        img.setCategory(category);
        img.setImageName(imageName);

        imageServiceRepository.save(img);
    }

    @Override
    public ImageDTO fetchImage(Long imageId) {
        Images image = imageServiceRepository.findById(imageId)
            .orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));

        return new ImageDTO(
            image.getImageId(),
            image.getImageUrl(),
            image.getCategory(),
            image.getPlaceName(),
            image.getPlaceId()
        );
    }

    @Override
    public String getFirstImageUrlByCategory(String category) {
        return imageServiceRepository
                .findFirstByCategoryIgnoreCaseOrderByImageIdAsc(category)
                .map(Images::getImageUrl)
                .orElse(null);
    }

    @Override
    public List<String> getAboutPlaceImages(Long placeId) {
        // Fetch all images associated with the placeId
        List<Images> images = imageServiceRepository.findImagesByPlaceId(placeId);

        // Extract the image URLs from the images
        return images.stream()
                     .map(Images::getImageUrl)
                     .collect(Collectors.toList());
    }

    @Override
    public String deleteImage(Long placeId)
    {
        // Get all image IDs associated with the given placeId
        List<Long> imageIds = imageServiceRepository.findImageIdsByPlaceId(placeId);
        
        // Check if there are any images associated with the placeId
        if (imageIds.isEmpty()) {
            throw new IllegalArgumentException("No images found for PlaceId: " + placeId);
        }

        // using flag to check if any image was deleted
        boolean isDeleted = false;
 
        //deleting each image
        for (Long imageId : imageIds) {
            Optional<Images> opt = imageServiceRepository.findById(imageId);

            // Check if the image exists or not  before deleting
            if (opt.isPresent()) {
                imageServiceRepository.deleteById(imageId);
                isDeleted = true;
            } else {

               throw new IllegalArgumentException("Images With Id "+imageId+" not found");
            }
        }

        // Return a success message if any image was deleted
        if (isDeleted) {
            return "Images with PlaceId " + placeId + " have been deleted.";
        } else {
            throw new IllegalArgumentException("No images found to delete for PlaceId: " + placeId);
        }
    }

}
