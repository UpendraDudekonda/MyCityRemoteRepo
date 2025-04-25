package com.mycity.place.serviceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycity.place.entity.Place;
import com.mycity.place.entity.TimeZone;
import com.mycity.place.repository.PlaceRepository;
import com.mycity.place.service.PlaceServiceInterface;
import com.mycity.shared.placedto.PlaceDTO;
import com.mycity.shared.timezonedto.TimezoneDTO;

@Service
public class PlaceServiceimpl implements PlaceServiceInterface
{
	@Autowired
    private PlaceRepository Placerepo;
	
	@Override
	public String addPlace(PlaceDTO dto) 
	{
      if(dto.getName()==null || dto.getName().trim().isEmpty())
    	  throw new IllegalArgumentException("Enter Place Name");
      else if(dto.getAbout()==null)
    	  throw new IllegalArgumentException("Mention About the Place");
      else if(dto.getHistory()==null)
          throw new IllegalArgumentException("Mention About the Place History");
      else if(dto.getCategory()==null)
    	  throw new IllegalArgumentException("Mention the Category");
      else if(dto.getPlaceDistrict()==null)
    	  throw new IllegalArgumentException("Mention the District of the Place");
      else if(dto.getTimezone()==null)
    	  throw new IllegalArgumentException("Mention the place TimeZone");
      else if(dto.getLatitude()==null)
    	  throw new IllegalArgumentException("Mention the Place Latitude");
      else if(dto.getLongitude()==null)
    	  throw new IllegalArgumentException("Mention the Place Longitude");
      
      TimeZone timezone=new TimeZone();
      timezone.setOpeningTime(dto.getTimezone().getOpeningTime());
      timezone.setClosingTime(dto.getTimezone().getClosingTime());
      timezone.setName(dto.getTimezone().getName());
      
      Place place=new Place();
      place.setPlaceName(dto.getName());
      place.setPlaceHistory(dto.getHistory());
      place.setAboutPlace(dto.getAbout());
      place.setPlaceCategory(dto.getCategory());
      place.setLatitude(dto.getLatitude());
      place.setLongitude(dto.getLongitude());
      place.setPlaceDistrict(dto.getPlaceDistrict());
      place.setPostedOn(LocalDate.now());
      
      place.setTimeZone(timezone);
      timezone.setPlace(place);
      
      //save the Place using Repo
      String name=Placerepo.save(place).getPlaceName();
      return "Place with name "+name+" saved Successfully";
      
	}

	@Override
	public PlaceDTO getPlace(Long placeId) 
	{
		//use repo to find the place based on given Id
		Optional<Place> opt=Placerepo.findById(placeId);
		if(opt.isPresent())
		{
			   Place place=opt.get();
			   PlaceDTO dto=new PlaceDTO();
			   //copy data from Place to PlaecDTO
			   dto.setName(place.getPlaceName());
			   dto.setAbout(place.getAboutPlace());
			   dto.setHistory(place.getPlaceHistory());
			   dto.setPlaceDistrict(place.getPlaceDistrict());
			   dto.setPostedOn(place.getPostedOn());
			   dto.setCategory(place.getPlaceCategory());
			   dto.setLatitude(place.getLatitude());
			   dto.setLongitude(place.getLongitude());
			   
			   TimeZone zone=place.getTimeZone();
			   
			   TimezoneDTO Tdto=new TimezoneDTO();
			   Tdto.setName(zone.getName());
			   Tdto.setOpeningTime(zone.getOpeningTime());
			   Tdto.setClosingTime(zone.getClosingTime());
		       
			   //set TimeZoneDTO to PlaceDTO
			   dto.setTimezone(Tdto);
			   
			   return dto;
		}
		else
			throw new IllegalArgumentException("Invalid Place Id..");
	}
	
	@Override
	public String updatePlace(Long placeId,PlaceDTO dto)
	{
	    //using repo to fetch the Place Details
		Optional<Place> opt=Placerepo.findById(placeId);
		if(opt.isPresent())
		{
			//get Place Object from Optional Object
			Place place=opt.get();
			//copy Data from PlaceDTO to Place Object
			place.setPlaceName(dto.getName());
			place.setAboutPlace(dto.getAbout());
			place.setPlaceHistory(dto.getHistory());
			place.setPlaceDistrict(dto.getPlaceDistrict());
			place.setPlaceCategory(dto.getCategory());
			place.setLatitude(dto.getLatitude());
			place.setLongitude(dto.getLongitude());
			
			
	        // Update existing TimeZone if present
	        TimezoneDTO timedto = dto.getTimezone();
	        if (timedto != null) 
	         {
	            TimeZone existingTZ = place.getTimeZone();
	            if (existingTZ != null) 
	            {
	                existingTZ.setName(timedto.getName());
	                existingTZ.setOpeningTime(timedto.getOpeningTime());
	                existingTZ.setClosingTime(timedto.getClosingTime());
	            } 
	            else 
	            {
	                // Optional: create a new timezone if not already present
	                TimeZone newTZ = new TimeZone();
	                newTZ.setName(timedto.getName());
	                newTZ.setOpeningTime(timedto.getOpeningTime());
	                newTZ.setClosingTime(timedto.getClosingTime());
	                newTZ.setPlace(place); // bind back to Place
	                place.setTimeZone(newTZ);
	            }
	        }
            
			//use repo to save the Updated Place Object
			Long idVal=Placerepo.save(place).getPlaceId();
			return "Place With Id ::"+idVal+" is Updated";
		}
		else
		{
			throw new IllegalArgumentException("Invalid Place Id..");
		}
	}

	@Override
	public Long getPlaceIdByName(String placeName) 
	{
		  //use repo
	      Long id=Placerepo.findPlaceIdByPlaceNameIgnoreCase(placeName);
		  return id;
	}
	
	@Override
	public String deletePlace(Long placeId) 
	{
	    //using repo to Delete the Place With Given Id
		Placerepo.deleteById(placeId);
		return "Place With Id ::"+placeId+" Deleted Successfully.";
	}
	
	@Override
	public List<PlaceDTO> getAllPlaces() 
	{
		System.out.println("PlaceServiceimpl=======================getAllPlaces()");
	    List<Place> places=Placerepo.findAll();
	    
	    List<PlaceDTO> dtos=new ArrayList<PlaceDTO>();
	    
	    for(Place p:places)
	    {
	    	PlaceDTO dt=new PlaceDTO();
	    	System.out.println("Place ::"+p);
            dt.setName(p.getPlaceName());
            dt.setAbout(p.getAboutPlace());
            dt.setHistory(p.getPlaceHistory());
            dt.setPlaceDistrict(p.getPlaceDistrict());
            dt.setLatitude(p.getLatitude());
            dt.setLongitude(p.getLongitude());
            dt.setCategory(p.getPlaceCategory());
	    	dt.setPostedOn(p.getPostedOn());
	    	TimeZone zone=p.getTimeZone();
	    	TimezoneDTO dto=new TimezoneDTO();
	    	dto.setName(zone.getName());
	    	dto.setOpeningTime(zone.getOpeningTime());
	    	dto.setClosingTime(zone.getClosingTime());
	    	
	    	dt.setTimezone(dto);
	    	
	    	System.out.println("Place DTO ----->"+dt);
	    	//Add DTO to List
	    	dtos.add(dt);
	    }  
	    return dtos;
	}	
}
