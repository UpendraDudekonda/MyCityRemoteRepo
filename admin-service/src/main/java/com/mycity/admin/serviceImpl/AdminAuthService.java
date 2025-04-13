package com.mycity.admin.serviceImpl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mycity.admin.entity.Admin;
import com.mycity.admin.repository.AdminAuthRepository;
import com.mycity.admin.service.AdminAuthInterface;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AdminAuthService implements AdminAuthInterface{

	
	private  final AdminAuthRepository AdminAuthRepository;
    private  final PasswordEncoder passwordEncoder; 
	
	
	@Override
    public Admin loginUser(String email, String password) {
        Admin admin = AdminAuthRepository.findByEmail(email);
        if (admin == null) {
            return null;
        }

        if (passwordEncoder.matches(password, admin.getPassword())) {
            return admin;
        }

        return null;
    }
	
	
	
}
