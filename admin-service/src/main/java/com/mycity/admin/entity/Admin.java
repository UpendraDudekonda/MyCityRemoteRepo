package com.mycity.admin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins") // Optional: Specify the table name
public class Admin {


	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String username;
	    private String email;
	    private String password; // Remember to hash passwords in a real application
	    private String firstName;
	    private String lastName;
	    private boolean activestatus;

	    public Admin() {
	    }
	    // Getters and Setters
	    
	    
	    // Constructor with required fields
	    public Admin(String username, String email, String password, String firstName, String lastName) {
	        this.username = username;
	        this.email = email;
	        this.password = password;
	        this.firstName = firstName;
	        this.lastName = lastName;
	    }

	}
	