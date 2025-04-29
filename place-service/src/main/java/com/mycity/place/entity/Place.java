package com.mycity.place.entity;


import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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

    @NonNull
    private Long categoryId;  

    private String categoryName; 
    
    @OneToOne(mappedBy = "place", cascade = CascadeType.ALL)
    private TimeZone timeZone;  

    @Column(nullable = true)
    private Double rating;

    private String placeDistrict;

    @Embedded
    private Coordinate coordinate;  
    
    @Column(nullable = false)
    private LocalDate postedOn;

}
