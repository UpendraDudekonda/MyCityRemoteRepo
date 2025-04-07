package com.mycity.user.serviceImpl;

import org.springframework.stereotype.Service;

import com.mycity.shared.userdto.UserResponseDTO;
import com.mycity.user.entity.User;
import com.mycity.user.exception.UserNotFoundException;
import com.mycity.user.repository.UserProfileRepository;
import com.mycity.user.service.UserProfileInterface;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService implements UserProfileInterface {

    private final UserProfileRepository userRepo;

    @Override
    public UserResponseDTO getUserById(String userIdStr) {
       
        if (!userIdStr.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid User ID format. Must be a number.");
        }

        Long userId = Long.parseLong(userIdStr);

       
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

       
        return new UserResponseDTO(
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                String.valueOf(user.getMobilenumber()),
                user.getUpdatedDate(),
                user.getCreatedDate()
        );
    }
}
