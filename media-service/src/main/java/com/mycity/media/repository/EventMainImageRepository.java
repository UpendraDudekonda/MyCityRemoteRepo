package com.mycity.media.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.media.entity.EventMainImage;

public interface EventMainImageRepository extends JpaRepository<EventMainImage, Long> {
	  Optional<EventMainImage>  findImageUrlByEventId(Long eventId);
}
