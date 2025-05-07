package com.mycity.place.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LocalCuisine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cuisineId;

    private String cuisineName;

    @ManyToOne
    @JoinColumn(name = "place_id", referencedColumnName = "placeId")  // Foreign key to Place
    private Place place;
}
