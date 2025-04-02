package com.mycity.merchant.service;

import org.springframework.stereotype.Service;

import com.mycity.merchant.dto.MerchantRegRequest;

@Service
public interface MerchantServiceInterface {

	String registerMerchant(MerchantRegRequest request);

}
