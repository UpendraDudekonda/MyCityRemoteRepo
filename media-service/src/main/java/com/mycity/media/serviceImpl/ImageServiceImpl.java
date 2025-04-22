package com.mycity.media.serviceImpl;

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
    public void uploadImage(MultipartFile file, Long placeId, String placeName, String category) {
        String imageUrl = cloudinaryHelper.saveImage(file); // your Cloudinary logic

        Images img = new Images();
        img.setImageUrl(imageUrl);
        img.setPlaceId(placeId);
        img.setPlaceName(placeName);
        img.setCategory(category);

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
}
