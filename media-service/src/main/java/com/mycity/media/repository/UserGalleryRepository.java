package com.mycity.media.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.media.entity.UserGallery;

public interface UserGalleryRepository extends JpaRepository<UserGallery,Long>
{
	List<UserGallery> findByDistrict(String district);
}
