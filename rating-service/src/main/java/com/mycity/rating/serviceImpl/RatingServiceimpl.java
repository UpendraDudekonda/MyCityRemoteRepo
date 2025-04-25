package com.mycity.rating.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.rating.entity.Rating;
import com.mycity.rating.repository.RatingRepository;
import com.mycity.rating.service.RatingServiceInterface;
import com.mycity.shared.ratingdto.RatingDTO;

@Service
public class RatingServiceimpl implements RatingServiceInterface 
{
	@Autowired
	private RatingRepository ratingRepo;
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	
    private static final String PLACE_SERVICE_NAME="place-service";
	private static final String PATH_TO_FIND_PLACEID="/place/getid/{placeName}";
	
	private static final String USER_SERVICE_NAME="user-service"; 
	private static final String PATH_TO_FIND_USERID="/user/getuserId/{userName}";
	
	@Override
	public String addRating(RatingDTO dto) 
	{
		if(dto.getPlaceName()==null || dto.getPlaceName().trim().isEmpty())
			throw new IllegalArgumentException("Place Name Cannot be Empty");
		else if(dto.getUserName()==null || dto.getUserName().trim().isEmpty())
			throw new IllegalArgumentException("User Name Cannot be Empty");
		
        Rating rating=new Rating();
        rating.setUserName(dto.getUserName());
        rating.setPlaceName(dto.getPlaceName());
        rating.setPostedDateTime(LocalDateTime.now());
        rating.setRatingValue(dto.getRatingValue());
        rating.setComment(dto.getComment());
        
	    //using WebClientBuilder to get PlaceId using PlaceName
	    String result1 = webClientBuilder
	            .build()
	            .get()
	            .uri("lb://"+PLACE_SERVICE_NAME+PATH_TO_FIND_PLACEID, dto.getPlaceName())
	            .retrieve()
	            .bodyToMono(String.class)
	            .block();
       System.out.println("Place Id :"+result1);
	    
	    //Converting String to Long
	    Long placeId=Long.parseLong(result1);
	    
	    //set PlaceId
	    rating.setPlaceId(placeId);
	    
	    //using WebClientBuilder to get UserId using userName
	    String result2 = webClientBuilder
	            .build()
	            .get()
	            .uri("lb://"+USER_SERVICE_NAME+PATH_TO_FIND_USERID, dto.getUserName())
	            .retrieve()
	            .bodyToMono(String.class)
	            .block();
	    
	    System.out.println("User Id ::"+result2);
	    
	    //Converting String to Long
	    Long userId=Long.parseLong(result2);
	    
	    //set UserId
	    rating.setUserId(userId);
        
        //using ratingRepo to save
        String Placename=ratingRepo.save(rating).getPlaceName();
        
        return "rating for the place ::"+Placename+" Added";
	}

}
