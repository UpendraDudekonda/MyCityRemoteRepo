package com.mycity.media.controller;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.media.service.UserGalleryService;

@RestController
@RequestMapping("/media/gallery")
public class UserGalleryController
{
   @Autowired
   private UserGalleryService service;
   
   @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public ResponseEntity<String> uploadImageToGallery(
           @RequestPart("image") MultipartFile file,
           @RequestParam Long userId,
           @RequestParam Long placeId,
           @RequestParam String placeName,
           @RequestParam String city,
           @RequestParam String district,
           @RequestParam String state) {

       if (file == null || file.isEmpty()) 
       {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image file is missing or empty.");
       }
       
       try 
       {
           String msg = service.uploadImages(file, userId, placeId, placeName, city, district, state);
           return ResponseEntity.ok(msg);
       }
       catch (Exception e) 
       {   
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("An error occurred while uploading the image.");
       }
   }
   
   @GetMapping("/getimages/{districtName}")
   public ResponseEntity<List<String>> getAllImages(@PathVariable String districtName) {
       try {
           List<String> imageUrls = service.getImages(districtName);
           if (imageUrls.isEmpty()) {
               return ResponseEntity.noContent().build(); // 204 No Content
           }
           return ResponseEntity.ok(imageUrls); // 200 OK
       } catch (IllegalArgumentException e) {
           return ResponseEntity.badRequest().body(Collections.emptyList()); // 400 Bad Request
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Collections.emptyList()); // 500 Internal Server Error
       }
   }
   
   
   @DeleteMapping("/deleteimage/{imageId}")
   public ResponseEntity<String> deleteImage(@PathVariable Long imageId) {
       try {
           String msg = service.deleteImage(imageId);
           return ResponseEntity.ok(msg);
       } catch (NoSuchElementException e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
       } catch (IllegalArgumentException e) {
           return ResponseEntity.badRequest().body(e.getMessage());
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
       }
   }


}
