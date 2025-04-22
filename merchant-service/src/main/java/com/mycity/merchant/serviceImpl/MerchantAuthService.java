package com.mycity.merchant.serviceImpl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mycity.merchant.entity.Merchant;
import com.mycity.merchant.repository.MerchantAuthRepository;
import com.mycity.merchant.service.MerchantAuthInterface;
import com.mycity.shared.merchantdto.MerchantRegRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantAuthService implements MerchantAuthInterface {

    private final MerchantAuthRepository merchantAuthRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String registerMerchant(MerchantRegRequest merchantRequest) {
        // Validate the input data
        validateMerchantInput(merchantRequest);

        // Check if email or GST number already exists
        if (merchantAuthRepo.existsByEmail(merchantRequest.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        if (merchantAuthRepo.existsByGstNumber(merchantRequest.getGstNumber())) {
            throw new IllegalArgumentException("GST Number already registered");
        }

        
        Merchant newMerchant = new Merchant();
        newMerchant.setName(merchantRequest.getName());
        newMerchant.setEmail(merchantRequest.getEmail());
        newMerchant.setPassword(passwordEncoder.encode(merchantRequest.getPassword())); // Encrypt password
        newMerchant.setPhoneNumber(merchantRequest.getPhoneNumber());
        newMerchant.setBusinessName(merchantRequest.getBusinessName());
        newMerchant.setBusinessAddress(merchantRequest.getBusinessAddress());
        newMerchant.setGstNumber(merchantRequest.getGstNumber());
        newMerchant.setRole("MERCHANT"); // Default role for new merchant

        
        merchantAuthRepo.save(newMerchant);

        return "Merchant registered successfully";
    }

    
	 @Override
	    public Merchant loginMerchant(String email, String password) {
	        Merchant merchant = merchantAuthRepo.findByEmail(email);
	        if (merchant == null) {
	            return null;
	        }

	        if (passwordEncoder.matches(password, merchant.getPassword())) {
	            return merchant;
	        }

	        return null;
	    }
	 
	 
	 
	 //Validation Logic For Merchant
	 private void validateMerchantInput(MerchantRegRequest merchant) {
	        if (merchant.getName() == null || merchant.getName().trim().isEmpty()) {
	            throw new IllegalArgumentException("Name is required");
	        }
	        if (merchant.getEmail() == null || merchant.getEmail().trim().isEmpty()) {
	            throw new IllegalArgumentException("Email is required");
	        }
	        if (!merchant.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
	            throw new IllegalArgumentException("Invalid email format");
	        }
	        if (merchant.getPassword() == null || merchant.getPassword().trim().isEmpty()) {
	            throw new IllegalArgumentException("Password is required");
	        }
	        if (merchant.getPhoneNumber() == null || merchant.getPhoneNumber().trim().isEmpty()) {
	            throw new IllegalArgumentException("Phone number is required");
	        }
	        if (merchant.getBusinessName() == null || merchant.getBusinessName().trim().isEmpty()) {
	            throw new IllegalArgumentException("Business name is required");
	        }
	        if (merchant.getBusinessAddress() == null || merchant.getBusinessAddress().trim().isEmpty()) {
	            throw new IllegalArgumentException("Business address is required");
	        }
	        if (merchant.getGstNumber() == null || merchant.getGstNumber().trim().isEmpty()) {
	            throw new IllegalArgumentException("GST number is required");
	        }
	        
	    }
	 	

}
