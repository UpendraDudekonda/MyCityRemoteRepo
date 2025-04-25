package com.mycity.review.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(name = "user_id")
    private Long userId; // ID of the user from the User service

    @Column(name = "place_id")
    private Long placeId; // ID of the place from the Place service

    @Column(columnDefinition = "TEXT")
    private String reviewDescription;

    private String placeName;
   
    @Column(name="review_Posted_On")
    private LocalDate postedOn;
    
    private String userName;
    
    private String imageUrl;


}

