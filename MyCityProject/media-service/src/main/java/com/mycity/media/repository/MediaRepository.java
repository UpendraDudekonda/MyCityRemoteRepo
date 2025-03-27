package com.mycity.media.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.media.entity.Media;

public interface MediaRepository extends JpaRepository<Media, Long> {

}
