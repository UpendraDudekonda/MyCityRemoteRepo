package com.mycity.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.shared.placedto.UserGalleryDTO;
import com.mycity.user.service.UserGalleryService;

@RestController
@RequestMapping("/user/gallery")
public class UserGalleryController {

	
	//AddGalleryPictures
	//RemoveGalleryPictures
	//UpdateGalleryPictures
    //GetGalleryPictures	

	@Autowired
	private UserGalleryService service;
	
	//to add images to User Gallery
	@PostMapping(value="/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadImage(@RequestPart List<MultipartFile> images,
			                                   @RequestPart UserGalleryDTO dto)
	{
		System.out.println("PlaceDiscoveriesController.uploadImage()");
		//use service
		String msg=service.uploadImagesToGallery(images,dto);
	    return ResponseEntity.ok(msg);
	}
	
	@GetMapping("/getimages/{districtName}")
	public ResponseEntity<List<String>> getImagesUsingDistrictName(@PathVariable String districtName)
	{
		System.out.println("UserGalleryController.getImagesUsingDistrictName()");
		List<String> urls=service.getImagesByDistrictName(districtName);
        return ResponseEntity.ok(urls);
	}
    
	@DeleteMapping("/deleteimage/{imageId}")
	public ResponseEntity<String> removePicturesFromGallery(@PathVariable Long imageId)
	{
		System.out.println("UserGalleryController.removePicturesFromGallery()");
		String result=service.deleteImage(imageId);
		return ResponseEntity.ok(result);
	}
}
