package com.mycity.rating.entity;

import java.time.LocalDateTime;

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
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId;

    @Column(name = "user_id")
    private Long userId; // ID of the user from the User service

    @Column(name = "place_id")
    private Long placeId; // ID of the place from the Place service

    private Integer ratingValue; // Assuming rating is an integer value (e.g., 1-5)

    @Column(columnDefinition = "TEXT")
    private String comment;

    private LocalDateTime postedDateTime;

}


