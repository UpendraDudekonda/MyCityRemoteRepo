package com.mycity.place.entity;

import java.util.List;

import com.mycity.place.config.StringListConverter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Place {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long placeId;

    @NonNull
    private String placeName;

    @NonNull
    @Column(columnDefinition = "TEXT")
    private String aboutPlace;

    @NonNull
    @Column(columnDefinition = "TEXT")
    private String placeHistory;

    @OneToOne(mappedBy = "place", cascade = CascadeType.ALL)
    private TimeZone timeZone; // Inverse side of the relationship

    @Column(nullable = true)
    private Double rating;

    private String placeCategory;

    private String placeDistrict;

    @Embedded
    private Coordinate coordinate;
    
  
    @Convert(converter = StringListConverter.class)
    @Column(name = "local_cuisines", columnDefinition = "TEXT")
    private List<String> localCuisines;

    @Convert(converter = StringListConverter.class)
    @Column(name = "nearby_hotels", columnDefinition = "TEXT")
    private List<String> nearByHotels;

    
    


}
