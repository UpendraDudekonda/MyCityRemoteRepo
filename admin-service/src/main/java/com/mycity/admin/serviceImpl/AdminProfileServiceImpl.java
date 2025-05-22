package com.mycity.admin.serviceImpl;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.admin.entity.Admin;
import com.mycity.admin.exception.AdminNotFoundException;
import com.mycity.admin.repository.AdminAuthRepository;
import com.mycity.admin.service.AdminProfileService;

import com.mycity.shared.admindto.AdminProfileResponse;
import com.mycity.shared.admindto.AdminProfileUpdateDTO;

@Service
public class AdminProfileServiceImpl implements AdminProfileService {

	@Autowired
	public  AdminAuthRepository adminAuthRepository; 
	
	@Autowired
	private PasswordEncoder passwordEncoder; // for update admin password

	
	
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
	            admin.getFirstName(),      // this was previously used as "username"
	            admin.getFirstName(),
	            admin.getLastName(),
	            admin.getPhoneNumber(),
	            admin.getDateOfBirth(),
	            admin.getCountry(),
	            admin.getCity(),
	            admin.getPostalCode()
	    );
	}
	
	 public void updateAdminFields(String adminIdStr, AdminProfileUpdateDTO request) {
	     
		// System.out.println("the request entered into service of adminprofilService");
		   
		 System.out.println("🧾 Incoming update request: " + request);

		 
			if (!adminIdStr.matches("\\d+")) {
		        throw new IllegalArgumentException("Invalid Admin ID format. Must be a number.");
		    }

		    Long adminId = Long.parseLong(adminIdStr);
		    
		    System.err.println(adminId);
		    System.out.println(adminId.getClass());

		 
		 Admin admin = adminAuthRepository.findById(adminId)
	                .orElseThrow(() -> new RuntimeException("Admin not found"));
		 
		 if (request.getFirstName() != null) admin.setFirstName(request.getFirstName());
		    if (request.getLastName() != null) admin.setLastName(request.getLastName());
		    if (request.getEmail() != null) admin.setEmail(request.getEmail());
		    if (request.getPhoneNumber() != null) admin.setPhoneNumber(request.getPhoneNumber());
		    if (request.getDateOfBirth() != null) admin.setDateOfBirth(request.getDateOfBirth());
		    if (request.getRole() != null) admin.setRole(request.getRole());
		    
		    if (request.getPassword() != null) admin.setPassword(passwordEncoder.encode(request.getPassword())); // Encrypt if needed

		    if (request.getCountry() != null) admin.setCountry(request.getCountry());
		    if (request.getCity() != null) admin.setCity(request.getCity());
		    if (request.getPostalCode() != null) admin.setPostalCode(request.getPostalCode());
	        adminAuthRepository.save(admin);
	    }


	@Override
	public void uploadUserImage(MultipartFile file, String userId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getImageUrlByUserId(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
