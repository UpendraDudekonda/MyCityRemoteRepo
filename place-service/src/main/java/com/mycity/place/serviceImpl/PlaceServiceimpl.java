package com.mycity.place.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mycity.place.entity.Place;
import com.mycity.place.entity.TimeZone;
import com.mycity.place.repository.PlaceRepository;
import com.mycity.place.service.PlaceServiceInterface;
import com.mycity.shared.placedto.PlaceDTO;

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
      timezone.setTimeZoneId(dto.getTimezone().getTimezoneId());
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
      
      place.setTimeZone(timezone);
      timezone.setPlace(place);
      
      //save the Place using Repo
      String name=Placerepo.save(place).getPlaceName();
      return "Place with name "+name+" saved Successfully";
      
	}

	@Override
	public Place getPlace(Long placeId) 
	{
		//use repo to find the place based on given Id
		Optional<Place> opt=Placerepo.findById(placeId);
		if(opt.isPresent())
			return opt.get();
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
			
			
			TimeZone time=new TimeZone();
			time.setTimeZoneId(dto.getTimezone().getTimezoneId());
            time.setOpeningTime(dto.getTimezone().getOpeningTime());
            time.setClosingTime(dto.getTimezone().getClosingTime());
            time.setName(dto.getTimezone().getName());
            
            place.setTimeZone(time);
            
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
	public String deletePlace(Long placeId) 
	{
	    //using repo to Delete the Place With Given Id
		Placerepo.deleteById(placeId);
		return "Place With Id ::"+placeId+" Deleted Successfully.";
	}
}
