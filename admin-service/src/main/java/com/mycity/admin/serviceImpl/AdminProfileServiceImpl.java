package com.mycity.admin.serviceImpl;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycity.admin.entity.Admin;
import com.mycity.admin.exception.AdminNotFoundException;
import com.mycity.admin.repository.AdminAuthRepository;
import com.mycity.admin.service.AdminProfileService;

import com.mycity.shared.admindto.AdminProfileResponse;

@Service
public class AdminProfileServiceImpl implements AdminProfileService {

	@Autowired
	public  AdminAuthRepository adminAuthRepository; 
	
	
	@Override
	public AdminProfileResponse getAdminById(String adminIdStr) {
		
		System.out.println("the request entered into service of adminprofilService");
	    
		if (!adminIdStr.matches("\\d+")) {
	        throw new IllegalArgumentException("Invalid Admin ID format. Must be a number.");
	    }

	    Long adminId = Long.parseLong(adminIdStr);
	    
	    System.err.println(adminId);
	    System.out.println(adminId.getClass());

	    Admin admin = adminAuthRepository.findById(adminId)
	            .orElseThrow(() -> new AdminNotFoundException("Admin not found with ID: " + adminId));

	    return new AdminProfileResponse(
	            admin.getId(),
	            admin.getEmail(),
	            admin.getRole(),
	            admin.getUsername()
	           
	    		);
	}

}
