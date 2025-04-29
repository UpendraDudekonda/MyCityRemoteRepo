package com.mycity.place.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "place_discoveries")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class PlaceDiscoveries
{
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private Long placeId;
  @NonNull
  private String placeName;
  @NonNull
  private String placeCategory;
  @NonNull
  private String imageUrl;
  
}
