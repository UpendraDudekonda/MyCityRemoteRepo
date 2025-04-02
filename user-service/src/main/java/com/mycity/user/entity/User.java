package com.mycity.user.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name= "users")
public class User {


	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String username;
	    private String email;
	    private String password; // Remember to hash passwords securely!
	    
	    private LocalDate registrationdate;
	    private long mobilenumber;

	    // Default constructor (required by JPA)
	    public User() {
	    }

	    // Constructor with required fields (adjust based on your registration process)
	       public User(String username, String email, String password) {
	           this.username = username;
	           this.email = email;
	           this.password = password;
	           
	       }



}
