package com.mycity.review.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.review.service.ReviewServiceInterface;
import com.mycity.shared.reviewdto.ReviewDTO;
import com.mycity.shared.reviewdto.ReviewSummaryDTO;

@RestController
@RequestMapping("/review")
public class PlaceReviewController 
{
	@Autowired
	private ReviewServiceInterface service;
	
	@PostMapping("/addreview")
	public ResponseEntity<String> addReview(@RequestBody ReviewDTO dto) {
	    ResponseEntity<String> response = service.addPlaceReview(dto);
	    return response;  // Use the status code returned by the service
	}

	
	@PutMapping("/updatereview/{reviewId}")
	public ResponseEntity<String> updateReview(@PathVariable Long reviewId,@RequestBody ReviewDTO dto)
	{
		//use service
		return new ResponseEntity<String>(service.updateReview(reviewId,dto),HttpStatus.OK);
	}
	
	@GetMapping("/getreviews/{placeId}")
	public ResponseEntity<List<ReviewSummaryDTO>> getAllReviews(@PathVariable Long placeId) //gives list of review's for a Particular place
	{
	    return new ResponseEntity<List<ReviewSummaryDTO>>(service.getUserReview(placeId),HttpStatus.FOUND);	 
	}
	
	@DeleteMapping("/deletereview/{reviewId}")
	public ResponseEntity<String> deleteReview(@PathVariable Long reviewId)
	{
		System.out.println("=========PlaceReviewController.deleteReview()============");
		return new ResponseEntity<String>(service.deleteReview(reviewId),HttpStatus.OK);
	}
}
