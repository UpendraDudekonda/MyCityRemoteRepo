package com.mycity.review.serviceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.InvalidUrlException;

import com.mycity.review.entity.Review;
import com.mycity.review.repository.ReviewRepository;
import com.mycity.review.service.ReviewServiceInterface;
import com.mycity.shared.reviewdto.ReviewDTO;
import com.mycity.shared.reviewdto.ReviewSummaryDTO;

import reactor.core.publisher.Mono;

@Service
public class ReviewServiceimpl implements ReviewServiceInterface {

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final String PLACE_SERVICE_NAME = "place-service";
    private static final String PATH_TO_FIND_PLACEID = "/place/getplaceid/{placeName}";

    private static final String USER_SERVICE_NAME = "user-service";
    private static final String PATH_TO_FIND_USERID = "/user/getuserId/{userName}";

    @Override
    public ResponseEntity<String> addPlaceReview(ReviewDTO dto) {
        if (dto.getImageUrl() == null || dto.getImageUrl().trim().isEmpty())
            throw new InvalidUrlException("Url Cannot be Empty");
        if (dto.getReviewDescription() == null || dto.getReviewDescription().trim().isEmpty())
            throw new IllegalArgumentException("Please Provide Description of Review.");

        Review review = new Review();
        review.setImageUrl(dto.getImageUrl());
        review.setPlaceName(dto.getPlaceName());
        review.setReviewDescription(dto.getReviewDescription());
        review.setUserName(dto.getUserName());
        review.setPostedOn(LocalDate.now());

        try {
            // Get placeId using place name
            Long result1 = webClientBuilder
                    .build()
                    .get()
                    .uri("lb://" + PLACE_SERVICE_NAME + PATH_TO_FIND_PLACEID, dto.getPlaceName())
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                        return Mono.error(new RuntimeException("Place not found or server error"));
                    })
                    .bodyToMono(Long.class)
                    .block();

            // If placeId is not found or invalid, return a NOT FOUND response
            if (result1 == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Invalid place name. Cannot find place ID.");
            }

            Long placeId = result1;
            review.setPlaceId(placeId);

            // Get userId using userName
            Long result2 = webClientBuilder
                    .build()
                    .get()
                    .uri("lb://" + USER_SERVICE_NAME + PATH_TO_FIND_USERID, dto.getUserName())
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response -> {
                        return Mono.error(new RuntimeException("User not found"));
                    })
                    .bodyToMono(Long.class)
                    .block();

            // If userId is not found, return 401 Unauthorized
            if (result2 == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not Exists , Login/Signup to add a review.");
            }

            review.setUserId(result2);

            Long id = reviewRepo.save(review).getReviewId();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Review with Id " + id + " saved.");

        } catch (RuntimeException e) {
            // Catch RuntimeException (from user not found or place not found)
            if (e.getMessage().contains("User not found")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User Not Exists , Login/Signup to add a review.");
            } else if (e.getMessage().contains("Place not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Invalid place name. Cannot find place ID.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Something went wrong while saving the review.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong while saving the review.");
        }
    }


    @Override
    public String updateReview(Long reviewId, ReviewDTO dto) {
        Optional<Review> opt = reviewRepo.findById(reviewId);

        if (opt.isPresent()) {
            Review review = opt.get();

            if (dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty())
                review.setImageUrl(dto.getImageUrl());

            if (dto.getReviewDescription() != null && !dto.getReviewDescription().trim().isEmpty())
                review.setReviewDescription(dto.getReviewDescription());

            Long id = reviewRepo.save(review).getReviewId();
            return "Review With Id " + id + " Updated.";
        } else {
            throw new IllegalArgumentException("Invalid Review Id");
        }
    }

    @Override
    public List<ReviewSummaryDTO> getUserReview(Long placeId) {
        List<Review> reviews = reviewRepo.findByPlaceId(placeId);
        List<ReviewSummaryDTO> dtos = new ArrayList<>();

        for (Review r : reviews) {
            ReviewSummaryDTO dto = new ReviewSummaryDTO();
            dto.setReviewDescription(r.getReviewDescription());
            dto.setImageUrl(r.getImageUrl());
            dto.setPlaceName(r.getPlaceName());
            dto.setUserName(r.getUserName());
            dto.setPostedOn(r.getPostedOn());
            dto.setRating(null); // Optional: add logic for user-provided rating
            dto.setUserImageUrl(null); // Optional: fetch from media-service using WebClient
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public String deleteReview(Long reviewId) {
        Optional<Review> opt = reviewRepo.findById(reviewId);
        if (opt.isPresent()) {
            reviewRepo.deleteById(reviewId);
            return "Review Deleted";
        } else {
            throw new IllegalArgumentException("Invalid Review Id");
        }
    }
}
