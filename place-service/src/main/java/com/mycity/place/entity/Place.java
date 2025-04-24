package com.mycity.place.entity;

import jakarta.persistence.*;
import lombok.*;

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
}
