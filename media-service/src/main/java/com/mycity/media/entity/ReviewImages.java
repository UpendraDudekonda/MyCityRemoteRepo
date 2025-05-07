package com.mycity.media.entity;

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
public class ReviewImages
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reviewImageId;
  
  private Long reviewId;  //get from review-service
  
  private Long placeId;  
   
  private String placeName; 
 
  private Long userId;
  
  private String userName;
  
  private String imageUrl;
}
