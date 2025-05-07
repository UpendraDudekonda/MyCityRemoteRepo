package com.mycity.media.serviceImpl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.media.entity.UserGallery;
import com.mycity.media.helper.CloudinaryHelper;
import com.mycity.media.repository.UserGalleryRepository;
import com.mycity.media.service.UserGalleryService;

@Service
public class UserGalleryServiceimpl implements UserGalleryService
{
   @Autowired
   private UserGalleryRepository galleryRepo;
   
   @Autowired
   private CloudinaryHelper cloudinaryHelper;

   @Override
   public String uploadImages(MultipartFile file, Long userId,
             Long placeId, String placeName,
           String city, String district, String state) {

       if (file == null || file.isEmpty()) 
       {
           return "Failed: Uploaded file is empty.";
       }

       try 
       {
           UserGallery gallery = new UserGallery();
           gallery.setUserId(userId);
           gallery.setPlaceId(placeId);
           gallery.setPlaceName(placeName); 
           gallery.setState(state);
           gallery.setDistrict(district);
           gallery.setCity(city);

           String url = cloudinaryHelper.saveImage(file);
           if (url == null || url.isEmpty()) {
               return "Failed: Image upload UnSuccessful.";
           }
           gallery.setImageUrl(url);

           UserGallery savedGallery = galleryRepo.save(gallery);
           Long idVal = savedGallery.getImageId();

           return "Success: Image with ID " + idVal + " saved.";
       }
       catch (Exception e) 
       {   
           return "Error: Could not upload image due to an internal error.";
       }
   }

   @Override
   public List<String> getImages(String district) {
       if (district == null || district.trim().isEmpty()) {
           throw new IllegalArgumentException("District must not be null or empty");
       }

       List<UserGallery> galleries = galleryRepo.findByDistrict(district);

       return galleries.stream()
               .map(UserGallery::getImageUrl)
               .filter(Objects::nonNull)
               .collect(Collectors.toUnmodifiableList());
   }

   @Override
   public String deleteImage(Long imageId) {
       return galleryRepo.findById(imageId)
               .map(image -> {
                   galleryRepo.deleteById(imageId);
                   return "Image with ID " + imageId + " has been successfully deleted.";
               })
               .orElseThrow(() -> new NoSuchElementException("Image with ID " + imageId + " not found."));
   }


}
