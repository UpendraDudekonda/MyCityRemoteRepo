package com.mycity.media.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.media.entity.AdminProfileImg;


public interface AdminProfileImgRepository extends JpaRepository<AdminProfileImg, Long> {

	Optional<AdminProfileImg> findByAdminId(String adminId);
}
