package com.mycity.merchant.serviceImpl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mycity.merchant.entity.Merchant;
import com.mycity.merchant.repository.MerchantRepository;
import com.mycity.merchant.service.MerchantServiceInterface;
import com.mycity.shared.merchantdto.MerchantRegRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantServiceInterface {

    private final MerchantRepository merchantRepo;
    private final PasswordEncoder passwordEncoder; 
   

    @Override
    public String registerMerchant(MerchantRegRequest request) {
    	if (request.getName() == null || request.getName().trim().isEmpty()) {
			throw new IllegalArgumentException("Name cannot be blank");
		}
		if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
			throw new IllegalArgumentException("Phone Number cannot be blank");
		}
		if (request.getEmail() == null || request.getEmail().trim().isEmpty() || !isValidEmail(request.getEmail())) {
			throw new IllegalArgumentException("Invalid email format");
		}
		if (request.getPassword() == null || request.getPassword().length() < 6) {
			throw new IllegalArgumentException("Password must be at least 6 characters long");
		}
		if (request.getBusinessName() == null || request.getBusinessName().trim().isEmpty()) {
			throw new IllegalArgumentException("Business Name cannot be blank");
		}
		if (request.getBusinessAddress() == null || request.getBusinessAddress().trim().isEmpty()) {
			throw new IllegalArgumentException("Business Address cannot be blank");
		}
		if (request.getGstNumber() == null || request.getGstNumber().trim().isEmpty()) {
			throw new IllegalArgumentException("GST Number cannot be blank");
		}
		
		// Check if the email already exists
		if (merchantRepo.findByEmail(request.getEmail()).isPresent()) {
			throw new IllegalArgumentException("Email address already registered");
		}

		// Encode the password
		String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Create new merchant
        Merchant merchant = new Merchant();
        merchant.setName(request.getName());
        merchant.setEmail(request.getEmail());
        merchant.setPassword(passwordEncoder.encode(request.getPassword())); // Encrypt password
        merchant.setPhoneNumber(request.getPhoneNumber()); // Fixed field name
        merchant.setBusinessName(request.getBusinessName());
        merchant.setBusinessAddress(request.getBusinessAddress()); 
        merchant.setGstNumber(request.getGstNumber()); // Added missing field
        merchant.setRole("MERCHANT");

        // Save to repository
        merchantRepo.save(merchant);

        return "Merchant registered successfully!";
    }

	private boolean isValidEmail(String email) {
		// TODO Auto-generated method stub
		return email.contains("@") && email.contains(".");
	}

}
