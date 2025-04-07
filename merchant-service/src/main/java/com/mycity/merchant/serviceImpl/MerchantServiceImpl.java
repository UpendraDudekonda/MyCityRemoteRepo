package com.mycity.merchant.serviceImpl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mycity.merchant.config.JwtService;
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
    private final JwtService jwtservice;

    @Override
    public String registerMerchant(MerchantRegRequest request) {
        // Validate request fields
        if (request.getEmail() == null || request.getPassword() == null || request.getName() == null) {
            throw new IllegalArgumentException("Merchant name, email, and password are required.");
        }

        // Check if email is already registered
        Optional<Merchant> existingMerchant = merchantRepo.findByEmail(request.getEmail());
        if (existingMerchant.isPresent()) {
            throw new RuntimeException("Merchant with this email already exists.");
        }

        // Create new merchant
        Merchant merchant = new Merchant();
        merchant.setName(request.getName());
        merchant.setEmail(request.getEmail());
        merchant.setPassword(passwordEncoder.encode(request.getPassword())); // Encrypt password
        merchant.setPhoneNumber(request.getPhoneNumber()); // Fixed field name
        merchant.setBusinessName(request.getBusinessName());
        merchant.setBusinessAddress(request.getBusinessAddress()); 
        merchant.setGstNumber(request.getGstNumber()); // Added missing field

        // Save to repository
        merchantRepo.save(merchant);

        return "Merchant registered successfully!";
    }
}
