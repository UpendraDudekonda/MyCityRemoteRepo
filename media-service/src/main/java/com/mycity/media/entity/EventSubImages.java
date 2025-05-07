package com.mycity.media.entity;


import java.util.List;

import com.mycity.media.config.StringListConverter;


import jakarta.persistence.Column;
import jakarta.persistence.Convert;

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
public class EventSubImages {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long imageId;

	private String eventName; // You might still want to store the name here

	@Column(name = "event_id")
	private Long eventId; // Storing the ID of the place from the other service


	 @Convert(converter = StringListConverter.class)
	    @Column(columnDefinition = "TEXT")
	private List<String> imageUrls;
	   
	 @Convert(converter = StringListConverter.class)
	    @Column(columnDefinition = "TEXT")
	private List<String> imageNames;

}
