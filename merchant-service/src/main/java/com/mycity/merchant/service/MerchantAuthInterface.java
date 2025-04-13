package com.mycity.merchant.service;

import com.mycity.merchant.entity.Merchant;
import com.mycity.shared.merchantdto.MerchantRegRequest;

public interface MerchantAuthInterface {

	
	Merchant loginMerchant(String email, String password);
	

	String registerMerchant(MerchantRegRequest merchantRequest);



	

}
