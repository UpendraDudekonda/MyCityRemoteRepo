package com.mycity.shared.merchantdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MerchantProfileResponse {

		private Long id;
	  	private String name;
	    private String email;
	    private String phoneNumber;
	    private String businessName;
	    private String businessAddress;
	    private String gstNumber;
}
