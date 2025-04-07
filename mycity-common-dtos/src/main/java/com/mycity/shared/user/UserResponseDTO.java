package com.mycity.shared.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private String id;
    private String name;
    private String email;
    private String role;
    private String phone;
    private LocalDateTime updatedDate; 
    private LocalDateTime createdDate;
    
}