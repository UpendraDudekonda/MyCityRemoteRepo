package com.mycity.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {


	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String username;
	    
	    private String email;
	    
	    private String password; // Remember to hash passwords securely!
	    
	    private LocalDateTime createdDate;
	    
	    private LocalDateTime updatedDate;

	    
	    private long mobilenumber;
	    
	    private String role;

}
