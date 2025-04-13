package com.mycity.shared.userdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResponse {
	private Long id; 
    private String email;
    private String role;

    // constructor, getters, setters
}
