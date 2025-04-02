package com.mycity.client.dto;

import lombok.Data;

@Data
public class ClientMerchantRegRequest {
		
	 	private String name;
	    private String email;
	    private String password;
	    private String phoneNumber;
	    private String businessName;
	    private String businessAddress;
	    private String gstNumber; 
}
