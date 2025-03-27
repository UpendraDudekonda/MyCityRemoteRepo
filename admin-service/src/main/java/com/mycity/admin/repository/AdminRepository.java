package com.mycity.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.admin.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long>{
	
}
