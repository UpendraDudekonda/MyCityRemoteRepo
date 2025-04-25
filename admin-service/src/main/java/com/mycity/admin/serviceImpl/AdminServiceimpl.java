package com.mycity.admin.serviceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.mycity.admin.service.AdminServiceInterface;
import com.mycity.shared.admindto.AdminPlaceResponseDTO;
import com.mycity.shared.placedto.PlaceDTO;

@Service
public class AdminServiceimpl implements AdminServiceInterface
{

	@Autowired
	private WebClient.Builder webClientBuilder;
 
	
	private static final String PATH_TO_GET_LIST_OF_PLACES="/place/getall";
	private static final String SERVICE_NAME="place-service";
	
	@Override
	public List<AdminPlaceResponseDTO> getAllPlaceDetails() 
	{
		System.out.println("AdminServiceimpl.getAllPlaceDetails()");
		//using Web Client to make API calls
	     List<PlaceDTO> places= webClientBuilder
	    		.build()
	            .get()
	            .uri("lb://"+SERVICE_NAME+PATH_TO_GET_LIST_OF_PLACES)
	            .retrieve()
	            .bodyToMono(new ParameterizedTypeReference<List<PlaceDTO>>() {})
	            .block();
	     
	     List<AdminPlaceResponseDTO> dtos=new ArrayList<AdminPlaceResponseDTO>();
	     for(PlaceDTO place:places)
	     {
             //Copying data from PlaceDTO to AdminPlaceResponseDTO...to display only name,Time,image,posted on details
	    	 AdminPlaceResponseDTO response=new AdminPlaceResponseDTO();
	    	 response.setCurrentDate(LocalDate.now());
	    	 response.setPostedOn(place.getPostedOn());
	    	 response.setImageUrl("");
	    	 response.setPlaceName(place.getPlaceName());
	    	 
	    	 System.out.println("Admin Response Data====> ::"+response);
	    	 //add response to List
	    	 dtos.add(response);
	     }
	     
	     return dtos;
	     
	}
  
}
