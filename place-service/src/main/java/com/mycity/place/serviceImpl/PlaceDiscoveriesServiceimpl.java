package com.mycity.place.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.place.entity.PlaceDiscoveries;
import com.mycity.place.exception.TooManyPlacesException;
import com.mycity.place.repository.PlaceDiscoveryRepository;
import com.mycity.place.repository.PlaceRepository;
import com.mycity.place.service.PlaceDiscoveriesInterface;
import com.mycity.place.service.PlaceServiceInterface;
import com.mycity.shared.placedto.PlaceDTO;
import com.mycity.shared.placedto.PlaceDiscoveriesDTO;

@Service
public class PlaceDiscoveriesServiceimpl implements PlaceDiscoveriesInterface
{
	@Autowired
	private PlaceDiscoveryRepository discoveryRepo;

	@Autowired
	private PlaceServiceInterface service;

	@Override
	public String addPlaceToDiscoveries(PlaceDiscoveriesDTO dto)
   {
	   //Limiting Places to add upto 10 ONLY
	   Long no_of_places=discoveryRepo.count();
	   
	   if(no_of_places>10)
	       throw new TooManyPlacesException("Cannot add more than top 10 places to discoveries");
	   
	   if(dto.getPlaceName()==null || dto.getPlaceName().trim().isEmpty())
		   throw new IllegalArgumentException("Place Name Cannot be Empty");
	   else if(dto.getPlaceCategory()==null || dto.getPlaceCategory().trim().isEmpty())
		   throw new IllegalArgumentException("Place Category Cannot be Empty");
	   else if(dto.getImageUrl()==null || dto.getImageUrl().trim().isEmpty())
		   throw new IllegalArgumentException("Provide a Valid Image URL");
	   
	   PlaceDiscoveries discovery=new PlaceDiscoveries();
	   //copy data from dto discovery
	   BeanUtils.copyProperties(dto, discovery);
	   Long pId=discoveryRepo.save(discovery).getPlaceId();
	   return "Place with Id "+pId+" added to top 10 discoveries";
	  
	}

	@Override
	public List<PlaceDiscoveriesDTO> getAllTopDisoveries() 
	{
		
		//using discoveryRepo
		List<PlaceDiscoveriesDTO> places=new ArrayList<PlaceDiscoveriesDTO>();
		List<PlaceDiscoveries> discoveries=discoveryRepo.findAll();
		for(PlaceDiscoveries place:discoveries)
		{
			PlaceDiscoveriesDTO discovery=new PlaceDiscoveriesDTO();
			//Copy Data from discoveries to places
			BeanUtils.copyProperties(place,discovery);
			places.add(discovery);
		}
		return places;
	}

	@Override
	public PlaceDTO getPlaceDetailsByName(String placeName) 
	{
	   //using placeService to get place Id from place_db using place name
	   Long pId=service.getPlaceIdByName(placeName);
	   //using placeId to get Complete info about Place
	   PlaceDTO place=service.getPlace(pId);
	   return place;
	}
    
}
