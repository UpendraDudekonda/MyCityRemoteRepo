package com.mycity.user.dto;

import java.time.LocalDate;

import lombok.Data;


@Data
public class Userdto {

		private Long id;
		private String firstName;
	    private String lastName;
	    private String email;
	    private String password; 
	    
	    private LocalDate registrationdate;
	    private long mobilenumber;
	    
	    private String role;
	    
	    
}
