package com.mycity.admin.serviceImpl;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;
import com.mycity.admin.config.JwtService;
import com.mycity.admin.entity.Admin;
import com.mycity.admin.repository.AdminRepository;
import com.mycity.admin.service.AdminServiceInterface;
import com.mycity.shared.admindto.AdminLoginRequest;



@Service
public class AdminService implements AdminServiceInterface {

	private  AdminRepository adminRepository;
	private  PasswordEncoder passwordEncoder;
	private  JwtService jwtservice;

	@Override
	public String loginAdmin(AdminLoginRequest request) {
		String email = request.getEmail();
		String password = request.getPassword();

		// Find the user by email
		Admin admin = adminRepository.findByEmail(email)
				.orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

		// Validate the password
		if (!passwordEncoder.matches(password, admin.getPassword())) {
			throw new BadCredentialsException("Invalid email or password");
		}

		System.out.println("Login Successfull");
		// Authentication successful, generate JWT token
		return jwtservice.generateToken(admin.getId(), admin.getEmail(), admin.getRole());
	}

}
