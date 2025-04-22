package com.mycity.media.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.media.entity.EventSubImages;

public interface EventSubImagesRepository extends JpaRepository<EventSubImages, Long> {
	List<EventSubImagesRepository> findImageUrlsByEventId(Long eventId);
}
