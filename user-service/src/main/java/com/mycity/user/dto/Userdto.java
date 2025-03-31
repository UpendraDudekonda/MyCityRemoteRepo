package com.mycity.user.dto;

import java.time.LocalDate;

public class Userdto {

		private Long id;
		private String firstName;
	    private String lastName;
	    private String email;
	    private String password; // Remember to hash passwords securely!
	    
	    private LocalDate registrationdate;
	    private long mobilenumber;
	    
	    //Generate Getters and Setters to Work
}
