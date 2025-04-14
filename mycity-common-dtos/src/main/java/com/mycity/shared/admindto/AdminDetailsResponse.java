package com.mycity.shared.admindto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDetailsResponse {
	private Long id; 
    private String email;
    private String role;

    // constructor, getters, setters
}
