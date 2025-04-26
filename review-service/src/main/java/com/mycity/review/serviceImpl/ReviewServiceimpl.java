package com.mycity.review.serviceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.InvalidUrlException;

import com.mycity.review.entity.Review;
import com.mycity.review.repository.ReviewRepository;
import com.mycity.review.service.ReviewServiceInterface;
import com.mycity.shared.reviewdto.ReviewDTO;
import com.mycity.shared.reviewdto.ReviewSummaryDTO;


@Service
public class ReviewServiceimpl implements ReviewServiceInterface 
{
	@Autowired
	private ReviewRepository reviewRepo;
     
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    private static final String PLACE_SERVICE_NAME="place-service";
	private static final String PATH_TO_FIND_PLACEID="/place/getplaceid/{placeName}";
	
	private static final String USER_SERVICE_NAME="user-service"; 
	private static final String PATH_TO_FIND_USERID="/user/getuserId/{userName}";
	
	
	@Override
	public String addPlaceReview(ReviewDTO dto) 
	{
	    if(dto.getImageUrl()==null || dto.getImageUrl().trim().isEmpty())
	    	throw new InvalidUrlException("Url Cannot be Empty");
	    else if(dto.getReviewDescription()==null || dto.getReviewDescription().trim().isEmpty())
	    	throw new IllegalArgumentException("Please Provide Description of Review..");
		
	    //creating Review Class Object
	    Review review=new Review();
	    
	    //copying data from dto to review 
	    review.setImageUrl(dto.getImageUrl());
	    review.setPlaceName(dto.getPlaceName());
	    review.setReviewDescription(dto.getReviewDescription());
	    review.setUserName(dto.getUserName());
	    review.setPostedOn(LocalDate.now());
	    

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
	  
	    //setting placeId to review 
	    review.setPlaceId(placeId);
	    
	    //using WebClientBuilder to get UserId using userName
	    Long result2 = webClientBuilder
	            .build()
	            .get()
	            .uri("lb://"+USER_SERVICE_NAME+PATH_TO_FIND_USERID, dto.getUserName())
	            .retrieve()
	            .bodyToMono(Long.class)
	            .block();
	    
	    System.out.println("User Id ::"+result2);
	    //Converting String to Long
	    Long userId=result2;
	    
	    //setting userId to review
	    review.setUserId(userId);
	    	    
	    //saving the review using reviewRepo
	    Long id=reviewRepo.save(review).getReviewId();
	    return "Review with Id "+id+" saved..";
	}

	@Override
	public String updateReview(Long reviewId, ReviewDTO dto) 
	{
	    Optional<Review> opt = reviewRepo.findById(reviewId);

	    if (opt.isPresent()) {
	        Review review = opt.get();

	        // Only update if the new value is not null and not blank (including just spaces)
	        if (dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty()) 
	            review.setImageUrl(dto.getImageUrl());

	        if (dto.getReviewDescription() != null && !dto.getReviewDescription().trim().isEmpty()) 
	            review.setReviewDescription(dto.getReviewDescription());
		        

	        //save the updated Data
	        Long Id=reviewRepo.save(review).getReviewId();
	        return "Review With Id " + Id + " Updated.";
	    } 
	    else
	    {
	        throw new IllegalArgumentException("Invalid Review Id");
	    }
	}

	@Override
	public List<ReviewSummaryDTO> getUserReview(Long placeId) 
	{
		 //using reviewRepo
	   	List<Review> reviews=reviewRepo.findByPlaceId(placeId);
        
	   	List<ReviewSummaryDTO> dtos=new ArrayList<ReviewSummaryDTO>();
	   	
		for(Review r:reviews)
		{
		   ReviewSummaryDTO dto=new ReviewSummaryDTO();
		   dto.setReviewDescription(r.getReviewDescription());
		   dto.setImageUrl(r.getImageUrl());
		   dto.setPlaceName(r.getPlaceName());
		   dto.setUserName(r.getUserName());
		   dto.setPostedOn(r.getPostedOn());
		   dto.setRating(null); 
		   dto.setUserImageUrl(null); //use WebClient to get UserImage from media-service based in UserId
		   //adding ReviewSummary DTO to list of dtos
		   dtos.add(dto);
		}
		return dtos;
	}

	@Override
	public String deleteReview(Long reviewId) 
	{
	    //check whether the review with given Id exists or not
		Optional<Review> opt=reviewRepo.findById(reviewId);
		if(opt.isPresent())
		{
			//use reviewRepo to delete review
			reviewRepo.deleteById(reviewId);
			return "Review Deleted";
		}
		else
		{
			throw new IllegalArgumentException("Invalid Review Id");
		}
	}
 
   
}
