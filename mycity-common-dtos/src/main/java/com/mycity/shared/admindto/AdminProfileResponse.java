package com.mycity.shared.admindto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor

public class AdminProfileResponse {
	
	private Long id;
	private String email;
	private String role;
	private String username;

}
