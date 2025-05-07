package com.mycity.user.serviceImpl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mycity.shared.userdto.UserDetailsResponse;
import com.mycity.user.entity.User;
import com.mycity.user.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserForgotpasswordService {
	
	private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Assuming you have a PasswordEncoder configured

    public UserDetailsResponse findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            // Map User entity to UserDetail DTO
        	UserDetailsResponse userDetail = new UserDetailsResponse();
            userDetail.setId(user.getId());
            userDetail.setEmail(user.getEmail());
            // Set other relevant details
            return userDetail;
        }
        return null;
    }

    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User with email " + email + " not found.");
        }

        // Encode and update the password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}
