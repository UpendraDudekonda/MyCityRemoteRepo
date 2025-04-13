package com.mycity.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mycity.admin.entity.Admin;

@Repository
public interface AdminAuthRepository extends JpaRepository<Admin, Long>{

	Admin findByEmail(String email);
	
	

}
