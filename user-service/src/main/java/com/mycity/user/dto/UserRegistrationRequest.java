package com.mycity.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRegistrationRequest {
	

	private String firstname;
	
	
	private String lastname;
	
	
	private String email;

    
    private String password;
    
    private long phoneNumber;

	
}
