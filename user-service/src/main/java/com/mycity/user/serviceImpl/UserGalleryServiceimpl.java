package com.mycity.user.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.placedto.UserGalleryDTO;
import com.mycity.user.service.UserGalleryService;

import jakarta.transaction.Transactional;

@Service
public class UserGalleryServiceimpl implements UserGalleryService 
{

	@Autowired
	private WebClientMediaService mediaService;
	
	
	@Autowired
	public WebClient.Builder webClientBuilder;
	
    private static final String PLACE_SERVICE_NAME = "place-service";
    private static final String PATH_TO_FIND_PLACEID = "/place/getplaceid/{placeName}";
    
	@Override
	@Transactional
	public String uploadImagesToGallery(List<MultipartFile> images, UserGalleryDTO dto) 
	{
		/*
	    //Extract userId from request header
	    String userIdHeader = servletRequest.getHeader("X-User-Id");
	    if (userIdHeader == null) {
	        throw new RuntimeException("Missing user ID in request header");
	    }

	    Long userId;
	    try {
	        userId = Long.parseLong(userIdHeader);
	    } catch (NumberFormatException e) {
	        throw new RuntimeException("Invalid user ID format in header", e);
	    }

	    System.out.println("User ID --> " + userId);
        */
		
		/*
		Cookie[] cookies=request.getCookies();
		if(cookies!=null)
		{
			for(Cookie cookie:cookies)
			{
				if("USER".equals(cookie.getName()))
				{
					String userDetails=cookie.getValue();
					System.out.println("User Details ::"+userDetails);
				}
			}
		}
		else
		{
			throw new IllegalArgumentException("USer Cookie Not Found");
		}
		*/

	    Long placeId;
	    try {
	        placeId = webClientBuilder.build()
	                .get()
	                .uri("lb://"+PLACE_SERVICE_NAME+PATH_TO_FIND_PLACEID,dto.getPlaceName())
	                .retrieve()
	                .bodyToMono(Long.class)
	                .block();

	        if (placeId == null) {
	            throw new RuntimeException("No place ID returned for place name: " + dto.getPlaceName());
	        }

	    } catch (Exception e) {
	        throw new RuntimeException("Failed to retrieve place ID: " + e.getMessage(), e);
	    }

	    System.out.println("Place Id ::"+placeId);
	    //Upload images if provided
	    if (images != null && !images.isEmpty()) {
	        for (MultipartFile image : images) {
	            mediaService.uploadImageToGallery(
	                image,
	                2l,   //HardCoded
	                placeId,
	                dto
	            );
	        }
	    }

	    return "Images uploaded to Gallery for place '" + dto.getPlaceName(); //+ "' by user ID " + userId;
	}
	
	@Override
	public List<String> getImagesByDistrictName(String districtName) 
	{
		//use media-service
		List<String> urls=mediaService.getImagesByDistrict(districtName);
		return urls;
	}

	@Override
	public String deleteImage(Long imageId) 
	{
		//use media-service
		return mediaService.deleteImage(imageId);
		
	}
  
}
