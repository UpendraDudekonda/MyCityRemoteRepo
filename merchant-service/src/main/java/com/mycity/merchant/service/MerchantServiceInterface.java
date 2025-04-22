package com.mycity.merchant.service;

import org.springframework.stereotype.Service;

import com.mycity.shared.merchantdto.MerchantProfileResponse;
import com.mycity.shared.merchantdto.MerchantRegRequest;

@Service
public interface MerchantServiceInterface {

	String registerMerchant(MerchantRegRequest request);

	MerchantProfileResponse getMerchantById(String merchantId);

	

}
