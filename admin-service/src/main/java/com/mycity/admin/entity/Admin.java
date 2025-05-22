package com.mycity.admin.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "admins") // Optional: Specify the table name
@Data
public class Admin {

			@Id
	        @GeneratedValue(strategy = GenerationType.IDENTITY)
	        private Long id;

	        private String firstName;
	        private String lastName;
	        private String email;
	        private String phoneNumber;
	        private LocalDate dateOfBirth;
	        private String role;
	        private String password;

	        private String country;
	        private String city;
	        private String postalCode;

	        // Getters and setters...
	    


	}
	